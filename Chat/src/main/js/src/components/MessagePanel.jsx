import React from "react";
import {connect} from "react-redux";
import ChatItem from "./ChatItem";

class MessagePanel extends React.Component {
    scrollToBottom = () => this.messagesEnd.scrollIntoView({behavior: "smooth"});

    componentDidUpdate = () => this.scrollToBottom();

    render = () => (
        <div className='chat-content'>
            <div ref={(el) => {this.messagesEnd = el;}}/>
            {this.props.messageItems.map((item, index) =>
                <ChatItem key={index} type={item.type} value={item.message} showavatar={item.showavatar}
                    avatar={item.avatar} date={item.createdDate}/>
            )}
        </div>
    );
}

const mapStateToProps = state => ({messageItems: state.chatReducer.messageItems});

const mapDispatchToProps = () => ({});

export default connect(mapStateToProps, mapDispatchToProps)(MessagePanel);
