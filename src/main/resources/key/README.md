# JWT 키 생성

Mac에서 RSA 키 쌍을 생성하려면 OpenSSL을 사용할 수 있습니다. 터미널에서 다음 명령을 실행하여 키를 생성합니다.

## RSA private key 생성

```bash
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048
```

## RSA public key 생성

```bash
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

이 명령은 2048 비트의 RSA 키 쌍을 생성하며, private_key.pem 파일에 개인 키를 저장하고 public_key.pem 파일에 공개 키를 저장합니다. 이 키를 사용하려면 DER 형식으로 변환한 후
코드에서 사용하면 됩니다.

## PEM to DER format (private key)

```bash
openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
```

## PEM to DER format (public key)

```bash
openssl rsa -pubin -inform PEM -outform DER -in public_key.pem -out public_key.der
```

