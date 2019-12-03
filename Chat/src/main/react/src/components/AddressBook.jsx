import React from "react";
import {Menu} from "antd";
import {connect} from "react-redux";
import {Scrollbars} from "react-custom-scrollbars";

import AddFriend from "./AddFriend";
import {CustomAvatar} from "./Avatar";
import {handleChangeAddressBook, loadAddressBookList} from "../actions/AddressBook";
import {changeMessageHeader} from "../actions/Chat";

class AddressBook extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            current: [],
            newselect: []
        };
    }

    componentDidMount = () => this.props.loadAddressBookList();

    handleCurrentChange = event => {
        const {handleChangeAddressBook, changeMessageHeader, addressBookList} = this.props;
        const key = event.key;
        this.setState({current: [key], newselect: []});
        handleChangeAddressBook(addressBookList[key].userId);
        changeMessageHeader(addressBookList[key].name, addressBookList[key].avatar, false);
    };

    handleNewChange = event => {
        const {handleChangeAddressBook, changeMessageHeader, newAddressBookList} = this.props;
        const key = event.key;

        console.log(key);
        this.setState({newselect: [key], current: []});
        handleChangeAddressBook(newAddressBookList[key].userId);
        changeMessageHeader(newAddressBookList[key].name, newAddressBookList[key].avatar, false);
    };

    render() {
        return (
            <div className="d-flex flex-column full-height address-book-menu">
                <AddFriend/>
                <Scrollbars autoHide autoHideTimeout={500} autoHideDuration={200}>
                    {this.props.newAddressBookList.length > 0 && (
                        <div>
                            <hr className="hr-sub-menu-title"/>
                            <div className="sub-menu-title new-add"> New Friends
                                ({this.props.newAddressBookList.length})
                            </div>
                            <Menu theme="light" mode="inline" defaultSelectedKeys={[]}
                                selectedKeys={this.state.newselect} className="address-book new-address-book"
                                onSelect={this.handleNewChange}>
                                {this.props.newAddressBookList.map((item, index) =>
                                    <Menu.Item key={index}>
                                        <div style={{width: 60}}>
                                            <CustomAvatar type="user-avatar" avatar={item.avatar}/>
                                        </div>
                                        {
                                            item.isOnline
                                                ? <div className="status-point online"/>
                                                : <div className="status-point offline"/>
                                        }
                                        <div style={{overflow: "hidden", paddingTop: 5}}>
                                            <div className="user-name">{item.name}</div>
                                            <div className="history-message">{item.status}</div>
                                        </div>
                                    </Menu.Item>
                                )}
                            </Menu>
                        </div>
                    )}
                    <hr className="hr-sub-menu-title"/>
                    <div className="sub-menu-title">Friends ({this.props.addressBookList.length})</div>
                    <Menu theme="light" mode="inline" defaultSelectedKeys={[]} selectedKeys={this.state.current}
                        className="address-book" onSelect={this.handleCurrentChange}>
                        {this.props.addressBookList.map((item, index) =>
                            <Menu.Item key={index}>
                                <div style={{width: 60}}>
                                    <CustomAvatar type="user-avatar" avatar={item.avatar}/>
                                </div>
                                {
                                    item.isOnline
                                        ? <div className="status-point online"/>
                                        : <div className="status-point offline"/>
                                }
                                <div style={{overflow: "hidden", paddingTop: 5}}>
                                    <div className="user-name">{item.name}</div>
                                    <div className="history-message">{item.status}</div>
                                </div>
                            </Menu.Item>
                        )}
                    </Menu>
                </Scrollbars>
            </div>
        );
    }
}

function mapStateToProps(state) {
    return {
        addressBookList: state.addressBookReducer.addressBookList,
        newAddressBookList: state.addressBookReducer.newAddressBookList
    };
}

function mapDispatchToProps(dispatch) {
    return {
        loadAddressBookList() {
            dispatch(loadAddressBookList());
        },
        changeMessageHeader(avatar, title, groupchat) {
            dispatch(changeMessageHeader(avatar, title, groupchat));
        },
        handleChangeAddressBook(userId) {
            dispatch(handleChangeAddressBook(userId));
        }
    };
}

export default connect(mapStateToProps, mapDispatchToProps)(AddressBook);
