import React from "react";
import {Alert, Button, Input, Modal, Tag} from "antd";
import {CustomAvatar} from "./Avatar";
import $ from "jquery";
import {connect} from "react-redux";
import {addNewUserChatGroup, removeUserChatGroup, startNewChatGroup} from "../actions/Chat";

class StartChatGroup extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    };

    showModal = () => this.setState({visible: true});

    handleOk = () => {
        this.setState({visible: false});
        this.props.startNewChatGroup();
    };

    handleCancel = () => this.setState({visible: false});

    addMoreUsername = () => {
        const un = $("#add-user-name");
        this.props.addNewUserChatGroup(un.val());
        un.val("");
    };

    handleRemoveUsername = item => this.props.removeUserChatGroup(item);

    render = () => (
        <div>
            <div className="new-action-menu" onClick={this.showModal}>
                <a href="#">
                    <CustomAvatar type="new-avatar"/>
                    <div className="new-text">Start New Group Chat</div>
                </a>
            </div>
            <Modal title="Start New Chat Group" className="start-chat-group-modal" visible={this.state.visible}
                okText="Start" onOk={this.handleOk} cancelText="Cancel" onCancel={this.handleCancel} width="420px">
                {this.props.startChatGroupError && (
                    <Alert message={this.props.startChatGroupErrorMessage} type="error"/>
                )}
                <p className="model-label">Please enter user name:</p>
                <div className="first-line">
                    <Input id="add-user-name" className="add-user-name"
                        ref={ref => {this.ref = ref;}} onPressEnter={this.addMoreUsername}/>
                    <Button onClick={this.addMoreUsername} type="primary" shape="circle" icon="plus"/>
                </div>
                {this.props.startChatGroupList.length > 0 && (
                    <p className="model-label" style={{marginBottom: 3, marginTop: 10}}>Selected:</p>
                )}

                {this.props.startChatGroupList.map((item, index) =>
                    <Tag key={index} closable onClose={e => {
                        this.handleRemoveUsername(item);
                        e.preventDefault();
                    }} color="#f50">{item}</Tag>
                )}
            </Modal>
        </div>
    );
}

const mapStateToProps = state => ({
    startChatGroupList: state.chatReducer.startChatGroupList,
    startChatGroupError: state.chatReducer.startChatGroupError,
    startChatGroupErrorMessage: state.chatReducer.startChatGroupErrorMessage
});

const mapDispatchToProps = dispatch => ({
    addNewUserChatGroup: username => dispatch(addNewUserChatGroup(username)),

    removeUserChatGroup: username => dispatch(removeUserChatGroup(username)),

    startNewChatGroup: () => dispatch(startNewChatGroup())
});

export default connect(mapStateToProps, mapDispatchToProps)(StartChatGroup);
