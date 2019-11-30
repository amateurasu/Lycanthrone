import React from "react";
import {connect} from "react-redux";
import {CustomAvatar} from "./Avatar";

class ChatHeader extends React.Component {
    render = () => (
        <div className='chat-header'>
            <div style={{width: 50}}>
                {
                    this.props.header.groupchat
                        ? <CustomAvatar type="panel-group-avatar"/>
                        : <CustomAvatar type="panel-avatar" avatar={this.props.header.avatar}/>
                }
            </div>
            <div style={{overflow: "hidden", paddingTop: 5}}>
                <div className="panel-message">{this.props.header.title}</div>
            </div>
        </div>
    );
}

const mapStateToProps = state => ({header: state.chatReducer.messageHeader});

const mapDispatchToProps = () => ({});

export default connect(mapStateToProps, mapDispatchToProps)(ChatHeader);
