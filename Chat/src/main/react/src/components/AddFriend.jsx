import $ from "jquery";
import React from "react";
import {connect} from "react-redux";
import {Alert, Input, Modal} from "antd";

import {CustomAvatar} from "./Avatar";
import {addNewFriend, changeStateAddFriendPopup} from "../actions/AddressBook";

class AddFriend extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    };

    handleOk = e => {
        console.log(e);
        let $add = $("#add-user-name");
        const un = $add.val();
        $add.val("");
        this.props.addNewFriend(un);
    };

    handleCancel = () => this.props.changeStateAddFriendPopup(false);

    showModal = () => this.props.changeStateAddFriendPopup(true);

    render() {
        return (
            <div>
                <div className="new-action-menu" onClick={this.showModal}>
                    <a href="#">
                        <CustomAvatar type="new-avatar"/>
                        <div className="new-text">Add New Friend</div>
                    </a>
                </div>
                <Modal title="Add New Friend" okText="Add" cancelText="Cancel" width="420px"
                    visible={this.props.addFriendPopup} onOk={this.handleOk} onCancel={this.handleCancel}>
                    {this.props.addFriendError && < Alert message={this.props.addFriendErrorMessage} type="error"/>}
                    <p className="model-label">Please enter user name:</p>
                    <Input id="add-user-name" className="add-user-name" onPressEnter={this.handleOk}/>
                </Modal>
            </div>
        );
    }
}

const mapStateToProps = state => ({
    addFriendError: state.addressBookReducer.addFriendError,
    addFriendErrorMessage: state.addressBookReducer.addFriendErrorMessage,
    addFriendPopup: state.addressBookReducer.addFriendPopup
});

const mapDispatchToProps = dispatch => ({
    addNewFriend: username => dispatch(addNewFriend(username)),

    changeStateAddFriendPopup: state => dispatch(changeStateAddFriendPopup(state))
});

export default connect(mapStateToProps, mapDispatchToProps)(AddFriend);
