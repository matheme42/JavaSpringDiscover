export class SocketMessageType {
  public static connection: string = 'Connection';
  public static disconnection: string = 'Disconnection';
  public static friendshipConnection: string = 'FriendshipConnection';
  public static friendshipDisconnection: string = 'FriendshipDisconnection';
  public static friendshipInvitation: string = 'FriendshipInvitation';
  public static selfFriendshipInvitation: string = 'SelfFriendshipInvitation';
  public static friendshipInvitationReply: string = 'FriendshipInvitationReply';
  public static selfFriendshipInvitationReply: string =
    'selfFriendshipInvitationReply';
  public static friendshipRemove: string = 'FriendshipRemove';
  public static friendshipMessage: string = 'FriendshipMessage';
}
