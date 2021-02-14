import AWS from 'aws-sdk';
import {handler} from "./genkeypair";

describe("test", () => {

    /*
    const s3: AWS.S3 = new AWS.S3({
        "s3ForcePathStyle": true,
        "accessKeyId": "S3RVER",
        "secretAccessKey": "S3RVER",
        "endpoint": new AWS.Endpoint("http://localhost:8080")
    });
    */

    test("test", async () => {
        const res: any = await handler({}, {});
        expect(res.kid.length > 0).toBeTruthy();
    });
})
