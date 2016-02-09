/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nishkarma.cas.client.validation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.Protocol;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;


/**
 * Creates either a Cas30ProxyTicketValidator or a Cas30ServiceTicketValidator depending on whether any of the
 * proxy parameters are set.
 * <p/>
 * This filter can also pass additional parameters to the ticket validator.  Any init parameter not included in the
 * reserved list {@link org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter#RESERVED_INIT_PARAMS}.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class NishkarmaCas30ProxyReceivingTicketValidationFilter extends NishkarmaCas20ProxyReceivingTicketValidationFilter {

    /** Name of the attribute used to answer role membership queries */
    private String roleAttribute = "memberOf";

    /** Whether or not to ignore case in role membership queries */
    private boolean ignoreCase = true;	
	
    public NishkarmaCas30ProxyReceivingTicketValidationFilter() {
        super(Protocol.CAS3);
        this.defaultServiceTicketValidatorClass = NishkarmaCas30ServiceTicketValidator.class;
        this.defaultProxyTicketValidatorClass = NishkarmaCas30ProxyTicketValidator.class;
    }
    
    
	/**
	 * Template method that gets executed if ticket validation succeeds.
	 * Override if you want additional behavior to occur if ticket validation
	 * succeeds. This method is called after all ValidationFilter processing
	 * required for a successful authentication occurs.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 * @param assertion
	 *            the successful Assertion from the server.
	 */
	protected void onSuccessfulValidation(final HttpServletRequest request,
			final HttpServletResponse response, final Assertion assertion) {
		
		//DO Something here

		
		logger.debug("---onSuccessfulValidation");
		
		AttributePrincipal attributePrincipal = assertion.getPrincipal();

		Map<String, Object> assertionAttMap = assertion.getAttributes();

		logger.debug("onSuccessfulValidation----assertionAttMap.size():{}",
				assertionAttMap.size());
		
		
		Set<Map.Entry<String, Object>> attSet1 = assertionAttMap.entrySet();
		

		for (Map.Entry<String, Object> a1 : attSet1) {

			logger.debug("-----attribute key:{}, value:{}", a1.getKey(),
					a1.getValue());

		}

		logger.debug(
				"onSuccessfulValidation----attributePrincipal.getName():{}",
				attributePrincipal.getName());

		Map<String, Object> attMap = attributePrincipal.getAttributes();

		logger.debug("onSuccessfulValidation----attMap.size():{}",
				attMap.size());


		Set<Map.Entry<String, Object>> attSet2 = attMap.entrySet();

		for (Map.Entry<String, Object> a2 : attSet2) {

			logger.debug("-----attribute key:{}, value:{}", a2.getKey(),
					a2.getValue());

		}

		logger.debug("-----getRemoteUser(attributePrincipal):{}", getRemoteUser(attributePrincipal));
		logger.debug("-----isUserInRole(attributePrincipal,staff):{}", isUserInRole(attributePrincipal, "staff"));
		logger.debug("-----isUserInRole(attributePrincipal,staff):{}", isUserInRole(attributePrincipal, "staff2"));		
		
		//just debug end		
	}

	/**
	 * Template method that gets executed if validation fails. This method is
	 * called right after the exception is caught from the ticket validator but
	 * before any of the processing of the exception occurs.
	 *
	 * @param request
	 *            the HttpServletRequest.
	 * @param response
	 *            the HttpServletResponse.
	 */
	protected void onFailedValidation(final HttpServletRequest request,
			final HttpServletResponse response) {
		// nothing to do here.
		logger.info("onFailedValidation");
	}
	
    public String getRemoteUser(AttributePrincipal attributePrincipal) {
        return attributePrincipal.getName();
    }

    public boolean isUserInRole(AttributePrincipal attributePrincipal, String role) {
        if (CommonUtils.isBlank(role)) {
            logger.debug("No valid role provided.  Returning false.");
            return false;
        }

        if (attributePrincipal == null) {
            logger.debug("No Principal in Request.  Returning false.");
            return false;
        }

        if (CommonUtils.isBlank(roleAttribute)) {
            logger.debug("No Role Attribute Configured. Returning false.");
            return false;
        }

        final Object value = attributePrincipal.getAttributes().get(roleAttribute);

        if (value instanceof Collection<?>) {
        	
        	
        	for (final Object o : (Collection<?>) value) {
                if (rolesEqual(role, o)) {
                    logger.debug("User [{}] is in role [{}]: true", getRemoteUser(attributePrincipal), role);
                    return true;
                }
            }
        }

        final boolean isMember = rolesEqual(role, value);
        logger.debug("User [{}] is in role [{}]: {}", getRemoteUser(attributePrincipal), role, isMember);
        return isMember;
    }

    /**
     * Determines whether the given role is equal to the candidate
     * role attribute taking into account case sensitivity.
     *
     * @param given  Role under consideration.
     * @param candidate Role that the current user possesses.
     *
     * @return True if roles are equal, false otherwise.
     */
    private boolean rolesEqual(final String given, final Object candidate) {
    	
    	
    	if (candidate == null)	return false;
    	
        return ignoreCase ? given.equalsIgnoreCase(candidate.toString()) : given.equals(candidate);
    }	
}
