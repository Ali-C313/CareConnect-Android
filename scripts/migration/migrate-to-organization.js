// Kopiert die alten Collections (employees, patients, orders, employeeLocations)
// in eine neue Organisation. Die alten Daten bleiben unverändert liegen.
//
// Aufruf:
//   npm install
//   node migrate-to-organization.js <serviceAccount.json> "<Name der Organisation>" <E-Mail der Leitung>

const path = require('path');
const admin = require('firebase-admin');

const [, , keyPath, orgName, ownerEmail] = process.argv;
if (!keyPath || !orgName || !ownerEmail) {
  console.log('Aufruf: node migrate-to-organization.js <serviceAccount.json> "<Name>" <E-Mail der Leitung>');
  process.exit(1);
}

admin.initializeApp({ credential: admin.credential.cert(require(path.resolve(keyPath))) });
const db = admin.firestore();
const now = admin.firestore.Timestamp.now();

// Kopiert eine Collection inklusive Unter-Collections
async function copyCollection(source, target) {
  const snapshot = await source.get();
  for (const doc of snapshot.docs) {
    await target.doc(doc.id).set(doc.data());
    const subCollections = await doc.ref.listCollections();
    for (const sub of subCollections) {
      await copyCollection(sub, target.doc(doc.id).collection(sub.id));
    }
  }
  return snapshot.docs;
}

function generateCode() {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
  let code = '';
  for (let i = 0; i < 8; i++) code += chars[Math.floor(Math.random() * chars.length)];
  return code;
}

async function addMember(orgRef, orgId, uid, data, role) {
  await orgRef.collection('members').doc(uid).set({
    role,
    firstName: data.firstName || '',
    lastName: data.lastName || '',
    inviteCode: null,
    joinedAt: now
  });

  await db.collection('users').doc(uid).set({
    firstName: data.firstName || '',
    lastName: data.lastName || '',
    email: data.email || '',
    birthDate: data.birthDate || null,
    address: data.address || '',
    city: data.city || '',
    phone: data.phone || '',
    profileImageURL: data.profileImageURL || '',
    activeOrgId: orgId,
    createdAt: data.createdAt || now
  }, { merge: true });

  await db.collection('users').doc(uid).collection('memberships').doc(orgId).set({
    orgName,
    orgType: 'pflegedienst',
    role,
    joinedAt: now
  });
}

async function addInvite(orgRef, orgId, role, createdBy) {
  const code = generateCode();
  const data = { orgId, orgName, orgType: 'pflegedienst', role, createdBy, createdAt: now };
  await db.collection('invites').doc(code).set(data);
  await orgRef.collection('invites').doc(code).set(data);
  return code;
}

async function main() {
  const owner = await admin.auth().getUserByEmail(ownerEmail);
  const orgRef = db.collection('organizations').doc();
  const orgId = orgRef.id;

  await orgRef.set({ name: orgName, type: 'pflegedienst', address: '', ownerId: owner.uid, createdAt: now });
  await db.collection('directory').doc(orgId).set({ name: orgName, nameLower: orgName.toLowerCase(), type: 'pflegedienst', address: '' });
  console.log(`Organisation "${orgName}" angelegt: ${orgId}`);

  const employees = await copyCollection(db.collection('employees'), orgRef.collection('employees'));
  const patients = await copyCollection(db.collection('patients'), orgRef.collection('patients'));
  const orders = await copyCollection(db.collection('orders'), orgRef.collection('orders'));
  await copyCollection(db.collection('employeeLocations'), orgRef.collection('employeeLocations'));
  console.log(`Kopiert: ${employees.length} Mitarbeiter, ${patients.length} Patienten, ${orders.length} Aufträge`);

  for (const doc of employees) {
    await addMember(orgRef, orgId, doc.id, doc.data(), doc.id === owner.uid ? 'admin' : 'staff');
  }
  for (const doc of patients) {
    await addMember(orgRef, orgId, doc.id, doc.data(), 'patient');
  }

  // Leitung, falls sie noch nicht als Mitarbeiter existierte
  if (!employees.some((doc) => doc.id === owner.uid)) {
    const data = { email: owner.email, firstName: owner.displayName || '', lastName: '' };
    await orgRef.collection('employees').doc(owner.uid).set({ uid: owner.uid, ...data, role: 'Leitung', status: 'Verfügbar' });
    await addMember(orgRef, orgId, owner.uid, data, 'admin');
  }

  const staffCode = await addInvite(orgRef, orgId, 'staff', owner.uid);
  const patientCode = await addInvite(orgRef, orgId, 'patient', owner.uid);
  console.log(`Code für Mitarbeiter: ${staffCode}`);
  console.log(`Code für Patienten:   ${patientCode}`);
  console.log('Fertig.');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
