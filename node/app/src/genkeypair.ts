import crypto from 'crypto';
import AWS from 'aws-sdk';
import {v4} from 'uuid';

const { publicKey, privateKey } = crypto.generateKeyPairSync('rsa', {
    modulusLength: 2048
});
console.log("KeyPair generated.");

const keyId: string = v4();
console.log(keyId);

const s3: AWS.S3 = new AWS.S3();
const bucketName: string = process.env.BUCKET_NAME ? process.env.BUCKET_NAME : '';
console.log(bucketName);

s3.putObject({
        Key: keyId + '/id_rsa.pub',
        Body: publicKey.export( {
            type: 'spki',
            format: 'pem'
        }),
        Bucket: bucketName
    } , (err, data) => {
        if (err) {
            throw err;
        }
        console.log(JSON.stringify(data));
    });
s3.putObject({
        Key: keyId + '/id_rsa',
        Body: privateKey.export({
            type: 'pkcs8',
            format: 'pem'
        }),
        Bucket: bucketName
    }, (err, data) => {
        if (err) {
            throw err;
        }
        console.log(JSON.stringify(data));
    });
