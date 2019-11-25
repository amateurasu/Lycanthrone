import React from "react";

import {makeStyles} from "@material-ui/core/styles";
import InputLabel from "@material-ui/core/InputLabel";

import GridItem from "../../components/Grid/GridItem.jsx";
import GridContainer from "../../components/Grid/GridContainer.jsx";
import CustomInput from "../../components/CustomInput/CustomInput.jsx";
import Button from "../../components/CustomButtons/Button.jsx";
import Card from "../../components/Card/Card.jsx";
import CardHeader from "../../components/Card/CardHeader.jsx";
import CardAvatar from "../../components/Card/CardAvatar.jsx";
import CardBody from "../../components/Card/CardBody.jsx";
import CardFooter from "../../components/Card/CardFooter.jsx";

import avatar from "../../assets/img/faces/marc.jpg";

const styles = {
    cardCategoryWhite: {
        color: "rgba(255,255,255,.62)",
        margin: "0",
        fontSize: "14px",
        marginTop: "0",
        marginBottom: "0"
    },
    cardTitleWhite: {
        color: "#FFFFFF",
        marginTop: "0px",
        minHeight: "auto",
        fontWeight: "300",
        fontFamily: "'Roboto', 'Helvetica', 'Arial', sans-serif",
        marginBottom: "3px",
        textDecoration: "none"
    }
};

const useStyles = makeStyles(styles);

export default function UserProfile() {
    const classes = useStyles();
    return (
        <div>
            <GridContainer>
                <GridItem xs={12} sm={12} md={8}>
                    <Card>
                        <CardHeader color="primary">
                            <h4 className={classes.cardTitleWhite}>Edit Profile</h4>
                            <p className={classes.cardCategoryWhite}>Complete your profile</p>
                        </CardHeader>
                        <CardBody>
                            <GridContainer>
                                <GridItem xs={12} sm={12} md={5}>
                                    <CustomInput id="company-disabled" labelText="Company (disabled)"
                                        formControlProps={{fullWidth: true}}
                                        inputProps={{disabled: true}}/>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={3}>
                                    <CustomInput id="username" labelText="Username" formControlProps={{fullWidth: true}}/>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={4}>
                                    <CustomInput id="email-address" labelText="Email address"
                                        formControlProps={{fullWidth: true}}/>
                                </GridItem>
                            </GridContainer>

                            <GridContainer>
                                <GridItem xs={12} sm={12} md={6}>
                                    <CustomInput id="first-name" labelText="First Name" formControlProps={{fullWidth: true}}/>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={6}>
                                    <CustomInput id="last-name" labelText="Last Name"
                                        formControlProps={{fullWidth: true}}/>
                                </GridItem>
                            </GridContainer>
                            <GridContainer>
                                <GridItem xs={12} sm={12} md={4}>
                                    <CustomInput id="city" labelText="City" formControlProps={{fullWidth: true}}/>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={4}>
                                    <CustomInput id="country" labelText="Country"
                                        formControlProps={{fullWidth: true}}/>
                                </GridItem>
                                <GridItem xs={12} sm={12} md={4}>
                                    <CustomInput id="postal-code" labelText="Postal Code"
                                        formControlProps={{fullWidth: true}}/>
                                </GridItem>
                            </GridContainer>
                            <GridContainer>
                                <GridItem xs={12} sm={12} md={12}>
                                    <InputLabel style={{color: "#AAAAAA"}}>About me</InputLabel>
                                    <CustomInput id="about-me" labelText="Lamborghini Mercy, Your chick she so thirsty, I'm in that two seat Lambo."
                                        formControlProps={{fullWidth: true}}
                                        inputProps={{multiline: true, rows: 5}}/>
                                </GridItem>
                            </GridContainer>
                        </CardBody>
                        <CardFooter>
                            <Button color="primary">Update Profile</Button>
                        </CardFooter>
                    </Card>
                </GridItem>
                <GridItem xs={12} sm={12} md={4}>
                    <Card profile>
                        <CardAvatar profile>
                            <a href="#pablo" onClick={e => e.preventDefault()}>
                                <img src={avatar} alt="..."/>
                            </a>
                        </CardAvatar>
                        <CardBody profile>
                            <h6 className={classes.cardCategory}>CEO / CO-FOUNDER</h6>
                            <h4 className={classes.cardTitle}>Alec Thompson</h4>
                            <p className={classes.description}>Don{"'"}t be scared of the truth because we need to restart the human foundation in truth And I love you like Kanye loves Kanye I love Rick Owens’ bed design but the back is...</p>
                            <Button color="primary" round>Follow</Button>
                        </CardBody>
                    </Card>
                </GridItem>
            </GridContainer>
        </div>
    );
}
