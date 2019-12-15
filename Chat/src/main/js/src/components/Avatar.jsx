import React from "react";
import Gravatar from "react-gravatar";
import Ravatar from "react-avatar";

export default class Avatar extends React.Component {
    render = () => <div className="friend-avatar"/>;
}

export class MainAvatar extends React.Component {
    render = () => <div className="user-avatar"/>;
}

export class CustomAvatar extends React.Component {
    render() {
        const {avatar, type} = this.props;
        const customClassName = `custom-avatar ${type}`;

        switch (type) {
            case "main-avatar":
                return <Gravatar email={`${avatar}@gmail.com`} className={customClassName} default="identicon"/>;
            case "new-avatar":
                return <Avatar icon="plus" className={customClassName} style={{fontSize: 30}}/>;
            case "panel-avatar":
                return <Ravatar name={avatar} className={customClassName} size="50"/>;
            case "panel-group-avatar":
                return <Ravatar name="G" color="#001529" className={customClassName} size="50"/>;
            case "user-avatar":
                return <Ravatar name={avatar} className={customClassName} size="60"/>;
            case "group-avatar":
                return <Ravatar name="G" color="#001529" className={customClassName} size="60"/>;
            case "chat-avatar":
                return this.props.show
                    ? <Ravatar name={avatar} className={customClassName} size="40"/>
                    : <div className="mock-small-avatar"/>;
            default:
                return <Avatar className={customClassName}/>;
        }
    }
}

export class SmallAvatar extends React.Component {
    render = () => this.props.show
        ? <div className="small-avatar"/>
        : <div className="mock-small-avatar"/>;
}
