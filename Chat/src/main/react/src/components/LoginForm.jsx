import React from "react";
import {Button, Form, Icon, Input} from "antd";
import {withRouter} from "react-router-dom";
import {API} from "../api/API";
import {setJWT, setUserID} from "../utils/utils";

const FormItem = Form.Item;

class NormalLoginForm extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            serverValidation: {
                visible: false,
                validateStatus: "error",
                errorMsg: "Invalid username or password!"
            }
        };
    }

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((error, values) => {
            if (error) return;

            API.post(`/signin`, values).then(res => {
                setJWT(res.data.data.jwt);
                setUserID(res.data.data.userId);
                this.props.history.push("/");
            });
        });
    };

    render() {
        const {getFieldDecorator} = this.props.form;
        const validation = this.state.serverValidation;
        return (
            <Form onSubmit={this.handleSubmit} className="login-form">
                <FormItem>
                    {getFieldDecorator("userName", {
                        rules: [{required: true, message: "Please input your username!"}]
                    })(
                        <Input prefix={<Icon type="user" style={{color: "rgba(0,0,0,.25)"}}/>} placeholder="Username"/>
                    )}
                </FormItem>
                <FormItem>
                    {getFieldDecorator("password", {
                        rules: [{required: true, message: "Please input your Password!"}]
                    })(
                        <Input prefix={<Icon type="lock" style={{color: "rgba(0,0,0,.25)"}}/>} type="password"
                            placeholder="Password"/>
                    )}
                </FormItem>
                {validation.visible && (
                    <FormItem validateStatus={validation.validateStatus} help={validation.errorMsg}/>
                )}
                <FormItem>
                    <Button type="primary" htmlType="submit" className="login-form-button">Log in</Button>
                </FormItem>
            </Form>
        );
    }
}

export const LoginForm = withRouter(Form.create()(NormalLoginForm));
