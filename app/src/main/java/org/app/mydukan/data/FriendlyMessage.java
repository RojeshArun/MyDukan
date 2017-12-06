/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.app.mydukan.data;

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String client_ID;
    private Chat_Author Chat_Author;

    public FriendlyMessage() {

    }
    public FriendlyMessage(String text, String name, String photoUrl,Chat_Author Chat_Author) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.Chat_Author = Chat_Author;
    }


    public String getClient_ID() {
        return client_ID;
    }

    public void setClient_ID(String client_ID) {
        this.client_ID = client_ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    public Chat_Author getChat_Author() {
        return Chat_Author;
    }

    public void setChat_Author(Chat_Author Chat_Author) {
        this.Chat_Author = Chat_Author;
    }
}
