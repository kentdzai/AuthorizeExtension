package com.soueidan.sfs2x.eventHandlers;

import java.util.Arrays;
import java.util.List;

import com.smartfoxserver.v2.core.*;
import com.smartfoxserver.v2.entities.*;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.entities.variables.UserVariable;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.soueidan.sfs2x.AuthorizeExtension;
import com.soueidan.sfs2x.requestHandlers.CreateCustomRoomRequestHandler;

public class UserZoneJoinEventHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {		
		User user = (User) event.getParameter(SFSEventParam.USER); 
		String joinRoomName = (String) user.getSession().getProperty("room");
		
		trace("Joining room:", joinRoomName);
		
		UserVariable isRegistered = new SFSUserVariable("isRegistered", user.getSession().getProperty("isRegistered"));
		UserVariable session = new SFSUserVariable("session", user.getSession().getProperty("session"));
		
		List<UserVariable> userVariables = Arrays.asList(isRegistered, session);
		getApi().setUserVariables(user, userVariables);

		Room room = getParentExtension().getParentZone().getRoomByName(joinRoomName);
		
		if (room == null) {
			throw new SFSException("The " + joinRoomName + " Room was not found!");
		}
		
		if ( joinRoomName.equals(AuthorizeExtension.LOBBY)) {
			getApi().subscribeRoomGroup(user, CreateCustomRoomRequestHandler.GROUP_GAME);
		}
		
		getApi().joinRoom(user, room, null, false, null, true, true);

		trace("--------------------------------------------------------");
	}

}