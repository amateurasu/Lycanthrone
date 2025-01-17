import React from "react";
import {Button, Form, Icon, Input} from "antd";
import {connect} from "react-redux";
import {register} from "../actions/User";

const FormItem = Form.Item;

class NormalRegisterForm extends React.Component {
    state = {confirmDirty: false};

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (err) { return; }

            console.log("Received values of form: ", values);
            const {fullName, userName, password} = values;
            this.props.register({fullName, userName, password});
        });
    };

    handleConfirmBlur = (e) => {
        const value = e.target.value;
        this.setState({confirmDirty: this.state.confirmDirty || !!value});
    };

    compareToFirstPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && value !== form.getFieldValue("password")) {
            callback("Two passwords that you enter is inconsistent!");
        } else {
            callback();
        }
    };

    validateToNextPassword = (rule, value, callback) => {
        const form = this.props.form;
        if (value && this.state.confirmDirty) {
            form.validateFields(["rePassword"], {force: true});
        }
        callback();
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator("fullName", {
                        rules: [{required: true, message: "Please input your Full Name!"}]
                    })(
                        <Input prefix={<Icon type="idcard" style={{color: "rgba(0,0,0,.25)"}}/>}
                            placeholder="Fullname"/>
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("userName", {
                        rules: [{required: true, message: "Please choose your username!"}]
                    })(
                        <Input prefix={<Icon type="user" style={{color: "rgba(0,0,0,.25)"}}/>} placeholder="Username"/>
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("password", {
                        rules: [
                            {required: true, message: "Please input your Password!"},
                            {validator: this.validateToNextPassword}
                        ]
                    })(
                        <Input prefix={
                            <Icon type="lock" style={{color: "rgba(0,0,0,.25)"}}/>} type="password" placeholder="Password"/>
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("rePassword", {
                        rules: [
                            {required: true, message: "Please re-input your Password!"},
                            {validator: this.compareToFirstPassword}
                        ]
                    })(
                        <Input prefix={<Icon type="lock" style={{color: "rgba(0,0,0,.25)"}}/>} type="password"
                            placeholder="Confirm Password" onBlur={this.handleConfirmBlur}/>
                    )}
                </FormItem>
                <FormItem>
                    <Button type="primary" htmlType="submit" className="login-form-button">Register</Button>
                </FormItem>
            </Form>
        );
    }
}

const mapStateToProps = state => ({user: state.userReducer.user});

const mapDispatchToProps = dispatch => ({
    register: user => dispatch(register(user))
});

export const RegisterForm = connect(mapStateToProps, mapDispatchToProps)(Form.create()(NormalRegisterForm));
