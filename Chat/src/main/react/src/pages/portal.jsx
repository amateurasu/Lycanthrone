import React from "react";
import {Tabs} from "antd";
import {LoginForm} from "../components/LoginForm";
import {RegisterForm} from "../components/RegisterForm";
import {connect} from "react-redux";
import {changeTab} from "../actions/User";
import {isAuthenticated} from "../utils/utils";
import {Redirect} from "react-router-dom";

const TabPane = Tabs.TabPane;

class Portal extends React.Component {

    handleTabChanged = (activeKey) => this.props.changeTab(activeKey);

    render() {
        return !isAuthenticated()
            ? <Redirect to="/"/>
            : <div id="portal-container">
                <div className="logo"><img src="logo.png" alt="logo.png"/></div>
                <div id="authen-panel">
                    <Tabs activeKey={this.props.activeTabKey} onChange={this.handleTabChanged}>
                        <TabPane tab="Login" key="1">
                            <LoginForm/>
                        </TabPane>
                        <TabPane tab="Register" key="2"><RegisterForm/></TabPane>
                    </Tabs>
                </div>
            </div>;
    }
}

const mapStateToProps = state => ({activeTabKey: state.userReducer.activeTabKey});

const mapDispatchToProps = dispatch => ({
    changeTab: activeKey => dispatch(changeTab(activeKey))
});

export default connect(mapStateToProps, mapDispatchToProps)(Portal);
