"use strict";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            navBarVisible: false,
            error: null // {msg: "No error!"}
        };
    }

    handleNavBarToggle = () => this.setState({navBarVisible: !this.state.navBarVisible});

    handleError = error => this.setState({error});

    render() {
        const {navBarVisible: navBar, error} = this.state;
        return (
            <div>
                <NavBar visible={navBar} onToggle={this.handleNavBarToggle}/>

                <div id="container" className={"container" + (navBar ? " off-canvas" : "")}>
                    <Overlay visible={navBar}/>
                    <NavHamburger onClick={this.handleNavBarToggle}/>

                    <h1>{this.props.name}</h1>
                    <MainForm onError={this}/>

                    {
                        error
                            ? <Notification className={navBar ? " off-canvas" : ""} error={error}/>
                            : null
                    }
                </div>
            </div>
        );
    }
}

//region NAVIGATION
class NavBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    }

    handleToggle = () => {
        // this.setState({visible: !this.state.visible});
        this.props.onToggle();
    };

    render() {
        return (
            <div id="side-nav" className={"side-nav " + (this.props.visible ? "nav-show" : "")}>
                <a className="close-nav no-select" onClick={this.handleToggle}>&times;</a>
                {/*<a href="" className="no-select">Suprise, madafaka?</a>*/}
            </div>
        );
    }
}

class NavHamburger extends React.Component {
    handleClick = () => {
        this.props.onClick();
    };

    render() {
        return <span className="open-nav no-select" onClick={this.handleClick}>&#9776;</span>;
    }
}

class Overlay extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: props.visible};
    }

    changeState = (visible) => this.setState({visible});

    render() {
        return <div className={"overlay" + (this.props.visible ? "" : " hidden")}/>;
    }
}

//endregion NAVIGATION

//region MAIN FORM
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

//region CHANGE MODE
class CryptList extends React.Component {
    static recentEncrypt = () => ["bin", "hex", "b64"];
    static recentDecrypt = () => ["bin", "hex", "b64"];
    static all = () => ["bin", "hex", "b64", "uri", "uni", "vie", "txt"];

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
        const allCrypt = this.props.allCrypt.reduce((all, crypt) => {
            all.push(<Crypt type={crypt} key={crypt}/>);
            return all;
        }, []);
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
                    <ul id="all-crypt" className="group">{allCrypt}</ul>
                </div>
            </div>
        );
    }
}

class Crypt extends React.Component {
    render() {
        const {type} = this.props;
        return <li id={`choice_${type}`} className="choice col-m-4 col-3"/>;
    }
}

// endregion CHANGE MODE

class GroupUpper extends React.Component {
    render() {
        const recentEncrypt = CryptList.recentEncrypt().reduce((all, crypt) => {
            all.push(<a id={"choose"} className={`crypt_${crypt}`}>{crypt.toUpperCase()}</a>);
            return all;
        }, [
            <a id={`choose`} className="choose-crypt a-icon a-down"/>,
            <a id={`choose`} className="choose-crypt a-icon a-down"/>
        ]);

        const recentDecrypt = CryptList.recentDecrypt().reduce((all, crypt) => {

            return all;
        }, []);

        return (
            <div className="group upper">
                <div id="encrypt" className="group crypt col-5 col-s-5">{recentEncrypt}</div>
                <div className="col-2 col-s-2"><i id="swap" className="icon swap"/></div>
                <div id="decrypt" className="group crypt col-5 col-s-5">{recentDecrypt}</div>
            </div>
        );
    }
}

class GroupLower extends React.Component {
    handleInputChange = (e) => {
        console.log(e.target.value);
    };

    render() {
        /*<i id="go" className="float top-right icon right-arrow hidden"/>*/
        /*<i id="copy" className="float bottom-left icon copy hidden"/>*/
        return (
            <div className="group under">
                <div className="div-input col-6 col-m-6">
                    <textarea id="input" className="txt input" title="input" name="input" autoFocus
                        onInput={this.handleInputChange} onPaste={this.handleInputChange}/>
                    <i id="clear" className="btn float top-right icon cross hidden"/>
                </div>
                <div className="div-output col-6 col-m-6">
                    <div id="output" className={"txt output wordwrap"} title="output"/>
                </div>

                <ModeDropdown allCrypt={CryptList.all()}/>
            </div>
        );
    }
}

//endregion MAIN FORM

//region NOTIFICATION
class Notification extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    }

    handleToggle = () => {
        this.setState({visible: false});
    };

    render() {
        console.log("render noti");
        const {msg} = this.props.error;
        return (
            <div id="notify" className={"alert " + (this.state.visible ? "" : "hidden")}>
                <span className="btn-close no-select" onClick={this.handleToggle}>&times;</span>
                <p id="msg-content">{msg}</p>
            </div>
        );
    }
}

//endregion NOTIFICATION

ReactDOM.render(<App name="Encode & Decode"/>, document.getElementById("root"));
