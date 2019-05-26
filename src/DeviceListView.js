import React, {Fragment} from 'react'
import {connect} from "react-redux";
import {AndroidTwoTone} from '@material-ui/icons'
import {List, ListItem, ListItemIcon, ListItemText, TextField, Typography} from "@material-ui/core";
import {setDeviceId} from "./store/Actions";

class DeviceListView extends React.Component {

    state = {deviceId:''}

    getDeviceList = () => {
        return (
            <List dense>
                {this.props.devices.map(device => {
                    return (<ListItem key={device} button onClick={() => this.props.setDeviceId(device)}>
                        <ListItemIcon><AndroidTwoTone/></ListItemIcon>
                        <ListItemText primary={device}/>
                    </ListItem>)
                })}
            </List>
        );
    }

    handleDeviceId = (e)=>{
        this.setState({
            deviceId: e.target.value
        })
    }

    submitDeviceId = (e)=>{
        if (e.keyCode === 13){
            const {deviceId} = this.state
            if (deviceId){
                console.log("device id updated " + deviceId)
                this.props.setDeviceId(deviceId)
            }
        }
    }

    render() {
        return (
            <Fragment>
                <Typography variant={"h4"} style={{marginTop:100, color:'#797979'}}>Select Device to view logs</Typography>
                <Typography variant={"subtitle2"} style={{color:'#797979'}}>Enter Device Id manually or select from the list below.</Typography>
                <TextField
                    style={{marginTop:20}}
                    variant={"outlined"}
                    label={"Enter Device ID"}
                    margin={"dense"}
                    fullWidth
                    onChange={this.handleDeviceId}
                    onKeyDown={this.submitDeviceId}
                />
                {this.getDeviceList()}
            </Fragment>

        )
    }

}

const mapStateToProps = (state) => {
    return {devices: state.devices}
}

const mapDispatchToProps = {
    setDeviceId
}

export default connect(mapStateToProps, mapDispatchToProps)(DeviceListView)