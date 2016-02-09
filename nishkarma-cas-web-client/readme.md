# nishkarma-cas-web-client
This is the nishkarma-cas-web SSO Server's client.


##Preparing tomcat server
It need's two tomcat servers.
Each Server's server.xml is described in /docs/tomcat-cas-client-local.example.org and /docs/tomcat-cas-client-local.example-other.org

maven package

drop mywebapp.war in webapp directory.

Modify web.xml with /docs/tomcat-cas-client-local.example.org/web.xml and /docs/tomcat-cas-client-local.example-other.org/web.xml

##Running

Connect the brower to https//local.example.org:8143/mywebapp and login.

User Info is verified with request.getRemoteUser(), request.isUserInRole("admin").
When you click the User Info link, can view the user id, and roles.

After that, connect to https//local.example-others.org:8243/mywebapp/ and go to protected area.
You can find that the SSO works.




##reference
https://github.com/Jasig/java-cas-client

