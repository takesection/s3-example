import crypto from 'crypto';
import AWS, {AWSError} from 'aws-sdk';
import {v4} from 'uuid';
import {PutObjectOutput} from "aws-sdk/clients/s3";

declare const S3_CLIENT_CONFIGURATION: string;
declare const BUCKET_NAME: string;

export const handler = async (event: any, context: any) => {
    const keyId: string = v4();
    console.log(keyId);

    const { publicKey, privateKey } = crypto.generateKeyPairSync('rsa', {
        modulusLength: 2048
    });
    console.log("KeyPair generated.");

    const bucketName: string = BUCKET_NAME;
    const s3ClientConfiguration: string = S3_CLIENT_CONFIGURATION;

    const s3: AWS.S3 = 0 < s3ClientConfiguration.length ? new AWS.S3(JSON.parse(s3ClientConfiguration)) :  new AWS.S3();

    const pub: PutObjectOutput = await s3.putObject({
        Key: keyId + '/id_rsa.pub',
        Body: publicKey.export( {
            type: 'spki',
            format: 'pem'
        }),
        Bucket: bucketName
    }, (err, data) => {
        if (err) {
            throw err;
        }
        return data;
    }).promise();
    console.log(JSON.stringify(pub));

    const keyRes: PutObjectOutput = await s3.putObject({
        Key: keyId + '/id_rsa',
        Body: privateKey.export({
            type: 'pkcs8',
            format: 'pem'
        }),
        Bucket: bucketName
    }, (err: AWSError, data: PutObjectOutput) => {
        if (err) {
            throw err;
        }
        return data;
    }).promise();
    console.log(JSON.stringify(keyRes));

    return {
        'kid': keyId
    };
}
