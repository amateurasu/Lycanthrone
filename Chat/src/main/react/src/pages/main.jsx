import $ from "jquery";
import React from "react";
import {connect} from "react-redux";
import {Redirect} from "react-router-dom";
import {Button, Icon, Input, Layout, Menu} from "antd";

import Profile from "../components/Profile";
import ChatList from "../components/ChatList";
import ChatHeader from "../components/ChatHeader";
import {CustomAvatar} from "../components/Avatar";
import AddressBook from "../components/AddressBook";
import MessagePanel from "../components/MessagePanel";
import {isAuthenticated, isEmptyString} from "../utils/utils";
import {closeWebSocket, initialWebSocket, loadChatContainer, submitChatMessage} from "../actions/Chat";

const {Sider} = Layout;
const {TextArea} = Input;

class Main extends React.Component {
    constructor(props) {
        super(props);
        this.state = {menuaction: 1};
    };

    componentDidMount = () => {
        this.props.initialWebSocket();
    };

    componentWillUnmount = () => {};

    handleMainMenuChange = e => {
        this.setState({menuaction: e.key});
    };

    handleMessageEnter = e => {
        let charCode = e.keyCode || e.which;
        if (e.shiftKey) return;

        e.preventDefault();
        let message = e.target.value;
        if (!isEmptyString(message)) {
            this.props.submitChatMessage(message);
        }
        e.target.value = "";
    };

    handleSendClick = e => {
        const txtMsg = $("#messageTextArea");
        let message = txtMsg.val();
        if (!isEmptyString(message)) {
            this.props.submitChatMessage(message);
        }
        txtMsg.val("");
    };

    render() {
        return isAuthenticated()
            ? <Redirect to="/login"/>
            : <div style={{height: `100vh`}}>
                <Layout>
                    <Sider breakpoint="lg" collapsedWidth="0" onBreakpoint={() => {}}
                        onCollapse={() => {}} width="80" id="main-side-menu">
                        <CustomAvatar type="main-avatar" avatar={this.props.userName}/>
                        <div className="menu-separation"/>
                        <Menu theme="dark" mode="inline" defaultSelectedKeys={["1"]} onSelect={this.handleMainMenuChange}>
                            <Menu.Item key="1"><Icon type="message" style={{fontSize: 30}}/></Menu.Item>
                            <Menu.Item key="2"><Icon type="bars" style={{fontSize: 30}}/></Menu.Item>
                        </Menu>
                    </Sider>
                    <Sider breakpoint="lg" collapsedWidth="0" theme="light"
                        onBreakpoint={() => {}} onCollapse={() => {}} width="300" id="sub-side-menu">
                        <Profile/>
                        <div className="menu-separation"/>
                        {this.state.menuaction === 1 ? <ChatList/> : <AddressBook/>}
                    </Sider>
                    <div className='chat-container' style={{padding: 0}}>
                        <ChatHeader/>
                        <MessagePanel/>
                        <div className='chat-footer'>
                            <TextArea id="messageTextArea" onPressEnter={this.handleMessageEnter} rows={1}
                                placeholder="Type a new message" ref="messageTextArea"/>
                            <Button type="primary" onClick={this.handleSendClick}>Send</Button>
                        </div>
                    </div>
                </Layout>
            </div>;
    }
}

const mapStateToProps = state => ({userName: state.userReducer.userName});

const mapDispatchToProps = dispatch => ({
    initialWebSocket: () => dispatch(initialWebSocket()),

    closeWebSocket: () => dispatch(closeWebSocket()),

    loadChatContainer: sessionId => dispatch(loadChatContainer(sessionId)),

    submitChatMessage: message => dispatch(submitChatMessage(message))
});

export default connect(mapStateToProps, mapDispatchToProps)(Main);
