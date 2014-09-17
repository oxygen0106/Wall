package com.oxygen.data;

public class WallComment {
	private long mCommentID;
	private String mCommentContent;
	private String mCommentCreateTime;
	private String mCommenterName;
	private String mCommenterImage;
	private long mUserID;

	public long getCommentID() {
		return mCommentID;
	}

	public void setCommentID(long commentID) {
		this.mCommentID = commentID;
	}

	public String getCommentContent() {
		return mCommentContent;
	}

	public void setCommentContent(String commentContent) {
		this.mCommentContent = commentContent;
	}

	public String getCommentCreateTime() {
		return mCommentCreateTime;
	}

	public void setCommentCreateTime(String commentTime) {
		this.mCommentCreateTime = commentTime;
	}

	public String getCommenterName() {
		return mCommenterName;
	}

	public void setCommenterName(String commenterName) {
		this.mCommenterName = commenterName;
	}

	public String getCommenterImage() {
		return mCommenterImage;
	}

	public void setCommenterImage(String commenterImage) {
		this.mCommenterImage = commenterImage;
	}

	public long getUserID() {
		return mUserID;
	}

	public void setUserID(long userID) {
		this.mUserID = userID;
	}

}
