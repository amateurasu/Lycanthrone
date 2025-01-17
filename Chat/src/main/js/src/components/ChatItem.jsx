import React from "react";
import {SlideDown} from "react-slidedown";

import {CustomAvatar} from "./Avatar";

export default class ChatItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {showDate: false};
    };

    handleItemClick = () => this.setState({showDate: !this.state.showDate});

    render() {
        const cssClass = `chat-item-${this.props.type === 1 ? "owner" : "other"}`;
        const cssContentClass = `chat-item-content-${this.props.type === 1 ? "owner" : "other"}`;
        return (
            <div onClick={this.handleItemClick} className={`chat-item chat-item-outer ${cssClass}`}>
                <div className={`chat-item ${cssClass}`}>
                    <CustomAvatar type="chat-avatar" avatar={this.props.avatar} show={this.props.showavatar}/>
                    <div className={`chat-item-content ${cssContentClass}`}>{this.props.value}</div>
                </div>
                {this.state.showDate && (
                    <SlideDown>
                        <div className={"chat-item-date"}>{this.props.date}</div>
                    </SlideDown>
                )}
            </div>
        );
    }
}
