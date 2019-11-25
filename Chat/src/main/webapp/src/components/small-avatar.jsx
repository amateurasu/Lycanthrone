import React from "react";

class SmallAvatar extends React.Component {
    render() {
        return this.props.show
            ? <div className="small-avatar"/>
            : <div className="mock-small-avatar"/>;
    }
}

export default SmallAvatar;
