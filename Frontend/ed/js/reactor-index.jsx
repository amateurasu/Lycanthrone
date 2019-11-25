"use strict";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            overlayVisible: false
        };
    }

    toggleNavBar = () => {
        const state = {overlayVisible: !this.state.overlayVisible};
        console.log(state);
        this.setState(state);
    };

    render() {
        console.log(this.state.overlayVisible);
        return (
            <div>
                <NavBar visible={this.state.overlayVisible}/>

                <div id="container" className="container">
                    <Overlay visible={this.state.overlayVisible}/>
                    <NavHamburger onClick={this.toggleNavBar}/>

                    <h1>{this.props.name}</h1>
                    <MainForm/>

                    <Notification/>
                </div>
            </div>
        );
    }
}

class NavBar extends React.Component {
    render() {
        let className = "side-nav " + (this.props.visible ? "nav-show" : "");
        console.log(className);
        return (
            <div id="side-nav" className={className}>
                <a className="close-nav no-select">&times;</a>
                <a href="" className="no-select">Suprise, madafaka?</a>
            </div>
        );
    }
}

class NavHamburger extends React.Component {
    render() {
        return <span className="open-nav no-select" onClick={this.props.onClick}>&#9776;</span>;
    }
}

class Overlay extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: props.visible};
    }

    changeState = (visible) => {
        this.setState({visible});
    };

    render() {
        return <div className={this.props.visible ? "overlay" : "overlay hidden"}/>;
    }
}

class MainForm extends React.Component {
    render() {
        return (
            <div className="ed">
                <GroupUpper/>
                <GroupLower/>
            </div>
        );
    }
}

class GroupUpper extends React.Component {
    render() {
        return (
            <div className="group upper">
                <div id="encrypt" className="group crypt col-5 col-s-5"/>
                <div className="col-2 col-s-2"><i id="swap" className="icon swap"/></div>
                <div id="decrypt" className="group crypt col-5 col-s-5"/>
            </div>
        );
    }
}

class GroupLower extends React.Component {
    render() {
        /*<i id="go" className="float top-right icon right-arrow hidden"/>*/
        /*<i id="copy" className="float bottom-left icon copy hidden"/>*/
        return (
            <div className="group under">
                <div className="div-input col-6 col-m-6">
                    <textarea id="input" className="txt input" title="input" name="input" autoFocus/>
                    <i id="clear" className="btn float top-right icon cross hidden"/>
                </div>
                <div className="div-output col-6 col-m-6">
                    <div id="output" className={"txt output wordwrap"} title="output"/>
                </div>

                <ModeDropdown/>
            </div>
        );
    }
}

class AvailableCrypt extends React.Component {
    makeChoiceCrypt = e => resetCrypt(drop.ed === "en" ? source : target, e.target.id.substr(7));

    render(id, name) {
        return <li id="choice_${id}" className="choice col-m-4 col-3" onClick={this.makeChoiceCrypt}>${name}</li>;
    }
}

class ModeDropdown extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            // recentCrypt
        };
    }

    render() {
        return (
            <div id="drop-ctn" className="drop-ctn hidden">
                <div>
                    <input type="text" id="search" title="search" className="search" placeholder="Search..."/>
                    <i id="drop-close" className="btn float right-center icon cross"/>
                </div>
                <div className="choices">
                    <h3>Recent Crypts</h3>
                    <ul id="recent-crypt" className="group">{/*this.state.recentCrypt*/}</ul>
                    <h3>All Crypts</h3>
                    <ul id="all-crypt" className="group">{/*this.props.allCrypt*/}</ul>
                </div>
            </div>
        );
    }
}

class Notification extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: true};
    }

    close = () => {
        this.setState({visible: false});
    };

    render() {
        return (
            <div id="notify" className={"alert " + (this.state.visible ? "" : "hidden")}> {/*hidden*/}
                <span className="btn-close no-select" onClick={this.close}>&times;</span>
                <p id="msg-content">{this.props.msg}</p>
            </div>
        );
    }
}

ReactDOM.render(<App name="Encode & Decode"/>, document.getElementById("root"));
