package org.openiot.security.client.rest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestfulOAuthService {

	private static Logger log = LoggerFactory.getLogger(RestfulOAuthService.class);
	private static final int STATUS_SUCCESS = 201;
	private static final int STATUS_DELETE_SUCCESS = 200;

	private String casOAuthURL;

	public RestfulOAuthService(String casOAuthUrl) {
		this.casOAuthURL = casOAuthUrl;
	}

	public Token getAccessToken(OAuthCredentialsRest credentials) {
		Token token = null;
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(casOAuthURL);
		String params = "username=" + credentials.getUsername() + "&password=" + credentials.getPassword() + "&clientId=" + credentials.getKey()
				+ "&secret=" + credentials.getSecret();
		Response response = target.request().post(Entity.text(params));
		// Read output in string format
		log.debug("Status code: {}", response.getStatus());

		if (response.getStatus() == STATUS_SUCCESS) {
			Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(response.readEntity(String.class));
			if (matcher.matches())
				token = new Token(matcher.group(1), "");
		} else {
			log.warn("Invalid response code {} from CAS server!", response.getStatus());
			log.info("Response: {}", response.readEntity(String.class));
		}

		response.close();
		return token;
	}

	public boolean removeAccessToken(String token) {
		boolean deleted = false;
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(casOAuthURL + "/" + token);
		log.debug("sending request to delete token {}", token);
		Response response = target.request().delete();
		log.debug("Delete request sent for token {}", token);
		log.debug("Status code: {}", response.getStatus());
		if (response.getStatus() == STATUS_DELETE_SUCCESS) {
			deleted = true;
		} else {
			log.warn("Invalid response code {} from CAS server!", response.getStatus());
			log.info("Response: {}", response.readEntity(String.class));
		}

		return deleted;
	}

	class UsernamePassword {
		private String username;
		private String password;

		public UsernamePassword(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return "username=" + username + "&password=" + password;
		}
	}

}
