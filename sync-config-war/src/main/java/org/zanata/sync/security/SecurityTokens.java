/*
 * Copyright 2016, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.zanata.sync.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.sync.dto.ZanataAccount;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@SessionScoped
public class SecurityTokens implements Serializable {
    private static final Logger log =
            LoggerFactory.getLogger(SecurityTokens.class);

    private ZanataAccount account;

    public boolean hasAccess() {
        return account != null;
    }

    public ZanataAccount getAccount() {
        return account;
    }

    public void setAuthenticatedAccount(ZanataAccount authenticatedAccount) {
        this.account = authenticatedAccount;
    }

}
