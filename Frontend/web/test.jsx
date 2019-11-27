"strict mode";
// const {
//     colors, Button, CssBaseline, ThemeProvider, Typography, Container, makeStyles, createMuiTheme, Box, SvgIcon, Link
// } = MaterialUI;

// function App() {
//     return <Button variant="contained" color="primary">Hello World</Button>;
// }

// var Router = ReactRouter.Router;
// var Route = ReactRouter.Route;
// var IndexRoute = ReactRouter.IndexRoute;
// var Link = ReactRouter.Link;
// var browserHistory = ReactRouter.browserHistory;

const {
    BrowserRouter,
    Switch,
    Route,
    Link,
    useRouteMatch,
    useParams
} = ReactRouterDOM;

class App extends React.Component {
    render() {
        return (
            <BrowserRouter>
                <div>
                    <ul>
                        <li>
                            <Link to="/">Home</Link>
                        </li>
                        <li>
                            <Link to="/about">About</Link>
                        </li>
                        <li>
                            <Link to="/topics">Topics</Link>
                        </li>
                    </ul>

                    <Switch>
                        <Route path="/about">
                            <About/>
                        </Route>
                        <Route path="/topics">
                            <Topics/>
                        </Route>
                        <Route path="/">
                            <Home/>
                        </Route>
                    </Switch>
                </div>
            </BrowserRouter>
        );
    }
}

function Home() {
    return <h2>Home</h2>;
}

function About() {
    return <h2>About</h2>;
}

function Topics() {
    let match = useRouteMatch();

    return (
        <div>
            <h2>Topics</h2>

            <ul>
                <li>
                    <Link to={`${match.url}/components`}>Components</Link>
                </li>
                <li>
                    <Link to={`${match.url}/props-v-state`}>
                        Props v. State
                    </Link>
                </li>
            </ul>

            {/* The Topics page has its own <Switch> with more routes
             that build on the /topics URL path. You can think of the
             2nd <Route> here as an "index" page for all topics, or
             the page that is shown when no topic is selected */}
            <Switch>
                <Route path={`${match.path}/:topicId`}>
                    <Topic/>
                </Route>
                <Route path={match.path}>
                    <h3>Please select a topic.</h3>
                </Route>
            </Switch>
        </div>
    );
}

function Topic() {
    let {topicId} = useParams();
    return <h3>Requested topic ID: {topicId}</h3>;
}

ReactDOM.render(<App/>, document.getElementById("root"));

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
