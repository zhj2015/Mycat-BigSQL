/*
 * Copyright (c) 2013, OpenCloudDB/MyCAT and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software;Designed and Developed mainly by many Chinese 
 * opensource volunteers. you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License version 2 only, as published by the
 * Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Any questions about this component can be directed to it's project Web address 
 * https://code.google.com/p/opencloudb/.
 *
 */
package org.opencloudb.net.mysql;

import org.opencloudb.MycatSystem;
import org.opencloudb.config.ErrorCode;
import org.opencloudb.net.FrontSession;
import org.opencloudb.util.StringUtil;

/**
 * @author mycat
 */
public class KillHandler {

	public static void handle(String stmt, int offset, FrontSession session) {
		String id = stmt.substring(offset).trim();
		if (StringUtil.isEmpty(id)) {
			session.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD,
					"NULL connection id");
		} else {
			// get value
			long value = 0;
			try {
				value = Long.parseLong(id);
			} catch (NumberFormatException e) {
				session.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD,
						"Invalid connection id:" + id);
				return;
			}

			// kill myself
			if (value == session.getConInfo().getId()) {
				session.writeOK();
				return;
			}

			// get connection and close it
			FrontSession theSession = MycatSystem.getInstance()
					.getFrontSessions().remove(value);
			if (theSession != null) {
				theSession.close("killed");
				session.writeOK();
			} else {
				session.writeErrMessage(ErrorCode.ER_NO_SUCH_THREAD,
						"Unknown connection id:" + id);
			}
		}
	}

}