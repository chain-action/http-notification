## Application settings.
Advanced settings in file `config.yml`.

```yaml
app:
  kafka_topic: notifications-1 # Topic name in kafka
  coeff_retry: 1.901 # Pause in seconds before next repeat = a^n, a - coeff_retry, n - attempt number 
  max_repeat: 15 # Maximum number of attempts to send, optional, default 15
  max_delay: 43200000 # Maximum pause (ms) between attempts to send a notification, optional, default 43200000 (12 hours)

  http_accept_domain_debug:
    - '.+\.pipedream.net'
    - '(www.|)requestinspector.com'
    - '(www.|)requestwatch.com'
#    - 'testn1.free-ton.online'
  meta_verification_name: # meta tag name
    - ftpro-notify-verification
    - google-site-verification
  http_accept_user_agent: # user agent for robots.txt
    - 'FtPro-Notify-Bot'
    - '*'
  info_title: Service name
  info_descr: service description
  info_logo: https://service.domain/logo.svg
  info_support_surf: 0:a9ef47b6bec35e001d1f295b34b9ec9abc0ca5c8623de4f414b4fd0b0dc6ca08
mysql:
  host: 127.0.0.1
  port: 3306
  database: http_notification # utf8mb4_general_ci
  user: user
  password: 'not_safe_password'
  character: utf8mb4
kafka:
  server: notification.services.tonlabs.io:29092 # host kafka server localhost:29092
  group_id: group1
  password: my-super-secret-password
  username: kafka_user
  sasl_mechanisms: SCRAM-SHA-512
  security_protocol: sasl_plaintext
redis:
  host: localhost
  port: 6379
  database: 5
http_sender: # parameters for client nttp notifications 
  timeout: 30 # In seconds, optional, default 60.0
  connect_timeout: 20 # In seconds, optional, default 20
  user_agent: 'OkHttp.java' # User Agent, optional, default OkHttp.java
http_server: #parameters for API server 
  port: 8010
  cors_hosts:
#    - '*' # allow for all host
    - my.domain
influxdb:
  url: http://localhost:8086 # set null or remove this line if you don't want event logging
  token: 'my-super-secret-auth-token'
  org: my-org
  bucket: my-bucket

```

*The file must be located in the directory with the application
