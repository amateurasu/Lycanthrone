import React from "react";
import {connect} from "react-redux";
import {CustomAvatar} from "./Avatar";

class ChatHeader extends React.Component {
    render = () => {
        const {groupchat, avatar, title} = this.props.header;
        return (
            <div className='chat-header'>
                <div style={{width: 50}}>
                    {groupchat ? (
                        <CustomAvatar type="panel-group-avatar"/>
                    ) : (
                        <CustomAvatar type="panel-avatar" avatar={avatar}/>
                    )}
                </div>
                <div style={{overflow: "hidden", paddingTop: 5}}>
                    <div className="panel-message">{title}</div>
                </div>
            </div>
        );
    };
}

const mapStateToProps = state => ({header: state.chatReducer.messageHeader});

const mapDispatchToProps = () => ({});

export default connect(mapStateToProps, mapDispatchToProps)(ChatHeader);
