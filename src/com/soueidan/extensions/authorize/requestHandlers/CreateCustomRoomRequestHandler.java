package com.soueidan.extensions.authorize.requestHandlers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.smartfoxserver.v2.api.*;
import com.smartfoxserver.v2.api.CreateRoomSettings.RoomExtensionSettings;
import com.smartfoxserver.v2.entities.*;
import com.smartfoxserver.v2.entities.data.*;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.exceptions.*;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.soueidan.extensions.authorize.core.AuthorizeExtension;

public class CreateCustomRoomRequestHandler extends BaseClientRequestHandler {

	static public String GROUP_GAME = "game";
	static public int MAX_USERS = 2;
	
	private User userInviter;
	private User userInvitee;
	
	private String roomName;
	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		String invitee = params.getUtfString("invitee");
		
		roomName = generateRandomWords(1)[0];
		
		userInviter = user;
		userInvitee = getApi().getUserByName(invitee);				

		createRoom();
		
		ISFSObject data = new SFSObject();
		data.putUtfString("roomName", roomName);
		
		List<User> users = Arrays.asList(userInviter, userInvitee);
		
		AuthorizeExtension extension = (AuthorizeExtension) getParentExtension();
		extension.send("createCustomRoom", data, users); 
	}

	private void createRoom() {
		trace("Create room name:", roomName);
		
		RoomExtensionSettings extension = new RoomExtensionSettings("TawleExtension", "com.soueidan.games.tawle.core.TawleExtension");
		
		CreateRoomSettings setting = new CreateRoomSettings();
		setting.setGroupId(GROUP_GAME);
		setting.setGame(true);
		setting.setMaxUsers(MAX_USERS);
		//setting.setDynamic(true);
		//setting.setAutoRemoveMode(SFSRoomRemoveMode.);
		setting.setUseWordsFilter(true);
		setting.setName(roomName);
		setting.setHidden(false);
		setting.setRoomVariables(getRoomVariables());
		setting.setExtension(extension);
		
		try {
			getApi().createRoom(getParentExtension().getParentZone(), setting, userInviter, false, null, false, false);
		} catch ( SFSCreateRoomException err ) {
			trace(err.getMessage());
		}
	}
	
	private List<RoomVariable> getRoomVariables() {
		
		RoomVariable variableInvitee = new SFSRoomVariable("invitee", userInvitee.getName());
		RoomVariable variableInviter = new SFSRoomVariable("inviter", userInviter.getName());
		
		variableInvitee.setGlobal(true);
		variableInviter.setGlobal(true);
		
		return  Arrays.asList(variableInviter, variableInvitee);
	}
	
	private static String[] generateRandomWords(int numberOfWords)
	{
	    String[] randomStrings = new String[numberOfWords];
	    Random random = new Random();
	    for(int i = 0; i < numberOfWords; i++)
	    {
	        char[] word = new char[random.nextInt(8)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
	        for(int j = 0; j < word.length; j++)
	        {
	            word[j] = (char)('a' + random.nextInt(26));
	        }
	        randomStrings[i] = new String(word);
	    }
	    return randomStrings;
	}
}
