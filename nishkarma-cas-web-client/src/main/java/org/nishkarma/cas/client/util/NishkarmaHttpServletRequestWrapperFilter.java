package org.nishkarma.cas.client.util;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.configuration.ConfigurationKeys;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;

public class NishkarmaHttpServletRequestWrapperFilter extends AbstractConfigurationFilter {

    /** Name of the attribute used to answer role membership queries */
    private String roleAttribute;

    /** Whether or not to ignore case in role membership queries */
    private boolean ignoreCase;

    public void destroy() {
        // nothing to do
    }

    /**
     * Wraps the HttpServletRequest in a wrapper class that delegates
     * <code>request.getRemoteUser</code> to the underlying Assertion object
     * stored in the user session.
     */
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain filterChain) throws IOException, ServletException {
        final AttributePrincipal principal = retrievePrincipalFromSessionOrRequest(servletRequest);

        filterChain.doFilter(new CasHttpServletRequestWrapper((HttpServletRequest) servletRequest, principal),
                servletResponse);
    }

    protected AttributePrincipal retrievePrincipalFromSessionOrRequest(final ServletRequest servletRequest) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = (Assertion) (session == null ? request
                .getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION) : session
                .getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION));

        return assertion == null ? null : assertion.getPrincipal();
    }

    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        this.roleAttribute = getString(ConfigurationKeys.ROLE_ATTRIBUTE);
        this.ignoreCase = getBoolean(ConfigurationKeys.IGNORE_CASE);
    }

    final class CasHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final AttributePrincipal principal;

        CasHttpServletRequestWrapper(final HttpServletRequest request, final AttributePrincipal principal) {
            super(request);
            this.principal = principal;
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public String getRemoteUser() {
            return principal != null ? this.principal.getName() : null;
        }

        public boolean isUserInRole(final String role) {
            if (CommonUtils.isBlank(role)) {
                logger.debug("No valid role provided.  Returning false.");
                return false;
            }

            if (this.principal == null) {
                logger.debug("No Principal in Request.  Returning false.");
                return false;
            }

            if (CommonUtils.isBlank(roleAttribute)) {
                logger.debug("No Role Attribute Configured. Returning false.");
                return false;
            }

            final Object value = this.principal.getAttributes().get(roleAttribute);

            if (value instanceof Collection<?>) {
                for (final Object o : (Collection<?>) value) {
                    if (rolesEqual(role, o)) {
                        logger.debug("User [{}] is in role [{}]: true", getRemoteUser(), role);
                        return true;
                    }
                }
            }

            final boolean isMember = rolesEqual(role, value);
            logger.debug("User [{}] is in role [{}]: {}", getRemoteUser(), role, isMember);
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

}
