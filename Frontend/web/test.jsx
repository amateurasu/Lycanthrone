"strict mode";
// const {
//     colors, Button, CssBaseline, ThemeProvider, Typography, Container, makeStyles, createMuiTheme, Box, SvgIcon, Link
// } = MaterialUI;

// function App() {
//     return <Button variant="contained" color="primary">Hello World</Button>;
// }

var {Router, Route, IndexRoute, Link, browserHistory} = ReactRouter;

class RouteA extends React.Component {
    btnClickB = () => browserHistory.push("/b");
    btnClickC = () => browserHistory.push("/c");

    render() {
        return (
            <div>
                <h2>Route A</h2>
                <div>
                    <button className="btn btn-default" onClick={this.btnClickB}>Goto B</button>
                    <button className="btn btn-default" onClick={this.btnClickC}>Goto C</button>
                </div>
            </div>
        );
    }
}

class RouteB extends React.Component {
    btnClickA = () => browserHistory.push("/a");
    btnClickC = () => browserHistory.push("/c");

    render() {
        return (
            <div>
                <h2>Route B</h2>
                <div>
                    <button className="btn btn-default" onClick={this.btnClickA}>Goto A</button>
                    <button className="btn btn-default" onClick={this.btnClickC}>Goto C</button>
                </div>
            </div>
        );
    }
}

class RouteC extends React.Component {
    btnClickA = () => browserHistory.push("/a");
    btnClickB = () => browserHistory.push("/b");

    render() {
        return (
            <div>
                <h2>Route C</h2>
                <div>
                    <button className="btn btn-default" onClick={this.btnClickA}>Goto A</button>
                    <button className="btn btn-default" onClick={this.btnClickB}>Goto B</button>
                </div>
            </div>
        );
    }
}

ReactDOM.render(
    <Router history={browserHistory}>
        <Route path="/" component={RouteA}/>
        <Route path="/a" component={RouteA}/>
        <Route path="/b" component={RouteB}/>
        <Route path="/c" component={RouteC}/>
        <Route path="*" component={RouteA}/>
    </Router>,
    document.getElementById("root")
);

class Clock extends React.Component {
    constructor(props) {
        super(props);
        this.state = {date: new Date()};
    }

    componentDidMount() {
        this.timerID = setInterval(() => this.tick(), 1000);
    }

    componentWillUnmount() {
        clearInterval(this.timerID);
    }

    tick() {
        this.setState({date: new Date()});
    }

    render() {
        return (
            <div>
                <h1>Hello, world!</h1>
                <h2>It is {this.state.date.toLocaleTimeString()}.</h2>
            </div>
        );
    }
}

class Toggle extends React.Component {
    constructor(props) {
        super(props);
        this.state = {isToggleOn: true};

        // This binding is necessary to make `this` work in the callback
        // this.handleClick = this.handleClick.bind(this);
    }

    handleClick = () => {
        console.log("something");
        this.setState(state => ({isToggleOn: !state.isToggleOn}));
    };

    render() {
        return <button onClick={this.handleClick}>{this.state.isToggleOn ? "ON" : "OFF"}</button>;
    }
}

class App2 extends React.Component {
    render() {
        return (
            <div>
                <Clock/>
                <Toggle/>
            </div>
        );
    }
}
