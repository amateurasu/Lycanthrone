import React from "react";
import {Menu} from "antd";
import {connect} from "react-redux";
import {Scrollbars} from "react-custom-scrollbars";

import {CustomAvatar} from "./Avatar";
import StartChatGroup from "./StartChatGroup";
import {changeMessageHeader, loadChatContainer, loadChatList, userSelected} from "../actions/Chat";

class ChatList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {menuaction: 1};
    }

    componentDidMount = () => this.props.loadChatList();

    handleChangeChatItem = event => {
        this.props.userSelected(event.key);
        this.props.loadChatContainer(event.key);
        for (let i = 0; i < this.props.chatList.length; i++) {
            const chat = this.props.chatList[i];
            if (chat.sessionId === event.key) {
                this.props.changeMessageHeader(chat.name, chat.avatar, chat.groupchat);
            }
        }
    };

    render() {
        if (!this.props.chatList) {
            return "Loading...";
        }

        return (
            <div className="d-flex flex-column full-height">
                <StartChatGroup/>
                <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
                    <Menu theme="light" mode="inline" className="chat-list"
                        onSelect={this.handleChangeChatItem} selectedKeys={this.props.userSelectedKeys}>
                        {
                            this.props.chatList.map((item, index) =>
                                <Menu.Item key={item.sessionId}>
                                    <div style={{width: 60}}>
                                        {
                                            item.groupchat
                                                ? <CustomAvatar type="group-avatar"/>
                                                : <CustomAvatar type="user-avatar" avatar={item.avatar}/>
                                        }
                                    </div>
                                    {
                                        // FIXME <div className={item.unread > 0 ? "unread-item" : ""}
                                        item.unread > 0 ?
                                            <div className="unread-item" style={{overflow: "hidden", paddingTop: 5}}>
                                                <div className="user-name">{item.name}</div>
                                                <div className="history-message">{item.lastMessage}</div>
                                            </div>
                                            :
                                            <div style={{overflow: "hidden", paddingTop: 5}}>
                                                <div className="user-name">{item.name}</div>
                                                <div className="history-message">{item.lastMessage}</div>
                                            </div>
                                    }
                                    {item.unread > 0 ? <div className="unread">{item.unread}</div> : ""}
                                </Menu.Item>
                            )
                        }
                    </Menu>
                </Scrollbars>
            </div>
        );
    }
}

const mapStateToProps = state => ({
    chatList: state.chatReducer.chatList,
    userSelectedKeys: state.chatReducer.userSelectedKeys
});

const mapDispatchToProps = dispatch => ({
    loadChatList: () => dispatch(loadChatList()),
    loadChatContainer: sessionId => dispatch(loadChatContainer(sessionId)),
    changeMessageHeader: (avatar, title, groupchat) => dispatch(changeMessageHeader(avatar, title, groupchat)),
    userSelected: sessionId => dispatch(userSelected(sessionId))
});

export default connect(mapStateToProps, mapDispatchToProps)(ChatList);
