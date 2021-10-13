# Usage

- [Installation](INSTALL.md)
- [Protecting the recipient page](#protecting-the-recipient-page)


_Attention: we should replace localhost with Ip or domain of your service_

## Protecting the recipient page

- [Allowing access through robots.txt](#allowing-access-through-robotstxt)
- [Security code for the page](#security-code-for-the-page)
  - [Getting code via API](#getting-code-via-api)
  - [Offline code computation](#offline-code-computation)

## Allowing access through robots.txt

Add an entry to the `robots.txt` file entry where `FtPro-Notify-Bot` is a parameter from the `config.yml` config or via [api](#getting-code-via-api).
```txt
User-agent: FtPro-Notify-Bot
Allow: /
```

### Security code for the page
You need to add the `<meta name="..." content="...">` entry inside the head tag on the page
```html
<head>
...
  <meta name="ftpro-notify-verification" content="_69aad49845d88aee43e5b696a5b249abb91a69b18dee85eaf5c76d31970b04fc">
</head>
```

#### Getting code via API

Post a request to address `http://localhost:8010/v0/approve-code` with the `url` parameter equal to the computed URL
```shell
curl -X POST "http://localhost:8010/v0/approve-code" -F "url=https://testn1.free-ton.online/test_push.php"
```
You will get a JSON object
```json
{
  "result": {
    "metaName": "ftpro-notify-verification",
    "success": true,
    "robotsTxt": "FtPro-Notify-Bot",
    "metaUrl": "69aad49845d88aee43e5b696a5b249abb91a69b18dee85eaf5c76d31970b04fc",
    "metaMain": "69e9029faf0644ed77a6ea92e9195764b1c662d642b4861b221ebffb102d506d"
  },
  "id": "1",
  "jsonrpc": "2.0"
}
```

| Name | Description |
| ------------- |: -------------: |
| metaName | parameter `name` for meta tag |
| metaUrl | parameter `content` for the page meta tag |
| metaMain | parameter `content` for the main page meta tag | 

*_metaMain for Main URL including `/`_

#### Offline code computation
SHA 256 string

Pages: Web {URL} Notify, main page: Web {Main URL including /} Notify

__Examples__
```shell
echo -n Webhttps://testn1.free-ton.online/test_push.phpNotify | sha256sum
echo -n Webhttps://testn1.free-ton.online/Notify | sha256sum
```

### Parameter structure for creating API notification entity
Required to create a notification entity via the API. Available at `http://localhost:8010/v1/structure`, but only practical in v1 `http://localhost:8010/v1/structure`
```json
{
  "result": {
    "library": {
      "types": [
        "string",
        "int",
        "list",
        "object"
      ],
      "list": {
        "method": [
          "GET",
          "PUT",
          "POST",
          "JSONRPC"
        ]
      }
    },
    "success": true,
    "list": [
      {
        "descr": "Unique hash of the event ",
        "name": "hash",
        "type": "string",
        "required": true
      },
      {
        "descr": "url address",
        "name": "url",
        "type": "string",
        "required": true
      },
      {
        "descr": "List of available http methods ",
        "default": "POST",
        "name": "method",
        "type": "list",
        "required": false
      },
      {
        "descr": "Parameter name",
        "default": "param",
        "name": "query",
        "type": "list",
        "required": false
      },
      {
        "descr": "Authorization object",
        "name": "auth",
        "type": "object",
        "required": false
      }
    ]
  },
  "id": "1",
  "jsonrpc": "2.0"
}
```

This API can be used to automatically generate a user interface form.
For example, you can get a list of options for the `method` parameter at` library.list.method`

### Service Information

`http://localhost:8010/v0/info` 
```json
{
  "result": {
    "success": true,
    "info": {
      "descr": "service description",
      "logo": "https://service.domain/logo.svg",
      "title": "Service name",
      "support_surf": "0:a9ef47b6bec35e001d1f295b34b9ec9abc0ca5c8623de4f414b4fd0b0dc6ca08"
    }
  },
  "id": "1",
  "jsonrpc": "2.0"
}
```