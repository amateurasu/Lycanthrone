import React from "react";
import {Icon, Input} from "antd";
import {clearStorage, isEmptyString} from "../utils/utils";
import {API} from "../api/API";
import {withRouter} from "react-router-dom";
import {closeWebSocket} from "../actions/Chat";
import {connect} from "react-redux";
import {changeUserStatus, getProfile, logout} from "../actions/User";

class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.state = {status_box_state: true};
    }

    componentDidMount = () => {
        this.props.getProfile();
        this.setState({
            ...this.state,
            status_text: this.props.userStatus
        });
    };

    componentDidUpdate = () => {
        if (isEmptyString(this.state.status_text) && this.state.status_box_state) {
            this.state.status_text = "You are online";
            this.setState({
                ...this.state,
                status_text: this.state.status_text
            });
        }
        if (this.refs.statusInput)
            this.refs.statusInput.focus();
    };

    logOut = () =>
        API.post(`/signout`).then(res => {
            clearStorage();
            this.props.closeWebSocket();
            this.props.logout();
            this.props.history.push("/login");
        });

    openStatusBoxStateStatus = () => {
        this.state.status_text = this.props.userStatus === "You are online" ? "" : this.props.userStatus;
        this.setState({
            ...this.state,
            status_box_state: false,
            status_text: this.state.status_text
        });
    };

    closeStatusBoxStateStatus = () => {
        this.props.changeUserStatus(this.state.status_text);
        this.setState({
            ...this.state,
            status_box_state: true
        });
    };

    onChangeStatus = (e) =>
        this.setState({
            ...this.state,
            status_text: e.target.value
        });

    render = () => (
        <div className="wrapper">
            <div className="profile-left">
                <div className="box user-name">Hey! - {this.props.userFullName}</div>
                <div className="box status">
                    {this.state.status_box_state
                        ? <div onClick={this.openStatusBoxStateStatus}>{this.props.userStatus}</div>
                        :
                        <Input onBlur={this.closeStatusBoxStateStatus} onPressEnter={this.closeStatusBoxStateStatus}
                            placeholder="Please enter a status..." value={this.state.status_text}
                            ref="statusInput" onChange={this.onChangeStatus}/>
                    }
                </div>
            </div>
            <div className="profile-right logout">
                <a href="#" className="logout-a" onClick={this.logOut}>
                    <Icon type="logout" style={{fontSize: 36, color: "#444"}}/>
                </a>
            </div>
        </div>
    );
}

const mapStateToProps = state => ({
    userFullName: state.userReducer.userFullName,
    userName: state.userReducer.userName,
    userStatus: state.userReducer.userStatus
});

const mapDispatchToProps = dispatch => ({
    changeUserStatus: status => dispatch(changeUserStatus(status)),
    getProfile: () => dispatch(getProfile()),
    logout: () => dispatch(logout()),
    closeWebSocket: () => dispatch(closeWebSocket())
});

export default connect(mapStateToProps, mapDispatchToProps)(withRouter(Profile));
