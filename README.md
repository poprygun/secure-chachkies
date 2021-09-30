# Basic Spring Boot security settings

## Generate Keystore

```bash
keytool -genkeypair -alias chachkies -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore chachkies.p12 -validity 3650
```

## Generate CA Root

```bash
openssl genrsa -des3 -out localCARoot.key 2048
```

## Create CA Root Certificate

```bash
openssl req -x509 -new -nodes -key localCARoot.key -sha256 -days 3650 -out localCARoot.pem
```

Invalid error page in Chrome can by bypassed by typing `thisisunsafe`

## Users and Roles

Two roles are creted - `chachkies.user` and `chachkies.admin`

`chachkies.user` credentials are `user1\password` has permission to access [protected chachkies endpoint](https://localhost:8443/api/chachkies)
`chachkies.user` credentials are `admin1\password` has permission to access [protected actuator endpoint](https://localhost:8443/actuator)


## [OIDC Well Known Endpoint](https://idp.int.identitysandbox.gov/.well-known/openid-configuration)

