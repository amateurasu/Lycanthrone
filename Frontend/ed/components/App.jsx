"use strict";

// import MainForm from "./MainForm"

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
            <React.Fragment>
                <NavBar visible={navBar} onToggle={this.handleNavBarToggle}/>

                <div id="container" className={`container ${navBar ? "off-canvas" : ""}`}>
                    <Overlay visible={navBar}/>
                    <NavHamburger onClick={this.handleNavBarToggle}/>

                    <h1>{this.props.name}</h1>
                    <MainForm onError={this}/>

                    {error && <Notification className={navBar ? " off-canvas" : ""} error={error}/>}
                </div>
            </React.Fragment>
        );
    }
}

//region NAVIGATION
class NavBar extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    }

    handleToggle = () => this.props.onToggle();

    render() {
        return (
            <div id="side-nav" className={`side-nav ${this.props.visible ? "nav-show" : ""}`}>
                <i className="close-nav no-select" onClick={this.handleToggle}>&times;</i>
                <a href="?q=wtf?" className="no-select">Suprise, madafaka?</a>
            </div>
        );
    }
}

class NavHamburger extends React.Component {
    handleClick = () => this.props.onClick();

    render() {
        return <span className="open-nav no-select" onClick={this.handleClick}>&#9776;</span>;
    }
}

class Overlay extends React.Component {
    render() {
        return <div className={`overlay${this.props.visible ? "" : " hidden"}`}/>;
    }
}

//endregion NAVIGATION

//region NOTIFICATION
class Notification extends React.Component {
    constructor(props) {
        super(props);
        this.state = {visible: false};
    }

    handleToggle = () => this.setState({visible: false});

    render() {
        const {msg} = this.props.error;
        return msg && (
            <div id="notify" className={`alert${this.state.visible ? "" : " hidden"}`}>
                <span className="btn-close no-select" onClick={this.handleToggle}>&times;</span>
                <p id="msg-content">{msg}</p>
            </div>
        );
    }
}

//endregion NOTIFICATION

ReactDOM.render(<App name="Encode & Decode"/>, document.getElementById("root"));
