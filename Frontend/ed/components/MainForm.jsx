// export default
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

class GroupUpper extends React.Component {
    handleOpenChoices = e => this.props.onOpenChoices();

    getCrypt = list => list.reduce((all, crypt) => {
        all.push(<a id={"choose"} className={`crypt_${crypt}`} key={crypt}>{crypt.toUpperCase()}</a>);
        return all;
    }, []);

    render() {
        const recentEncrypt = this.getCrypt(CryptList.recentEncrypt());
        const recentDecrypt = this.getCrypt(CryptList.recentDecrypt());

        return (
            <div className="group upper">
                <div id="encrypt" className="group crypt col-5 col-s-5">
                    <a id="choose" className="choose-crypt a-icon a-down" onClick={this.handleOpenChoices}/>,
                    <a id="en_detect" className="active crypt_detect">AUTO DETECT</a>
                    {recentEncrypt}
                </div>
                <div className="col-2 col-s-2"><i id="swap" className="icon swap"/></div>
                <div id="decrypt" className="group crypt col-5 col-s-5">
                    <a id="choose" className="choose-crypt a-icon a-down" onClick={this.handleOpenChoices}/>
                    {recentDecrypt}
                </div>
            </div>
        );
    }
}

// endregion CHANGE MODE

class GroupLower extends React.Component {
    constructor(props) {
        super(props);
        this.state = {input: "", output: ""};
    }

    handleInput = e => {
        const input = e.target.value;
        const output = e.target.value; // process(input);
        this.setState({input, output});
    };

    render() {
        /*<i id="go" className="float top-right icon right-arrow hidden"/>*/
        /*<i id="copy" className="float bottom-left icon copy hidden"/>*/
        return (
            <div className="group under">
                <ModeDropdown allCrypt={CryptList.all()}/>
                <Input onInputChange={this.handleInput} value={this.state.input}/>
                <Output value={this.state.output}/>
            </div>
        );
    }
}

class Input extends React.Component {
    handleInput = e => this.props.onInputChange(e);

    render() {
        return (
            <div className="div-input col-6 col-m-6">
                <textarea id="input" className="txt input" title="input" name="input" autoFocus value={this.props.value}
                    onInput={this.handleInput} onPaste={this.handleInput} onChange={this.handleInput}/>
                <i id="clear" className="btn float top-right icon cross"/>
            </div>
        );
    }
}

class Output extends React.Component {
    render() {
        return (
            <div className="div-output col-6 col-m-6">
                <div id="output" className="txt output wordwrap" title="output">{this.props.value}</div>
            </div>
        );
    }
}
