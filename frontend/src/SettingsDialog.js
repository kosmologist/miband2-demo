import React from 'react'
import {
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField
} from "@material-ui/core";
import {connect} from "react-redux";
import {setDeviceId} from "./store/Actions";
import {initFirebase} from "./fire";


class SettingsDialog extends React.Component {

    state = {open: false, deviceId:this.props.deviceId}

    handleOpen = () => {
        this.setState({open: true})
    }

    handleClose = () => {
        this.setState({open: false})
    }

    handleDeviceId = (e)=>{
        this.setState({
            deviceId: e.target.value
        })
    }

    handleSubmit = ()=>{
        console.log('Device Id: ' + this.state.deviceId)
        this.props.setDeviceId(this.state.deviceId)
        initFirebase()
        this.handleClose()
    }

    render() {
        return (
            <Dialog
                open={this.state.open}
                onClose={this.handleClose}
            >
                <DialogTitle>Settings</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Set Streaming ID to view device specific logs
                        <TextField
                            variant={"outlined"}
                            label={"Device ID"}
                            margin={"dense"}
                            fullWidth
                            onChange={this.handleDeviceId}
                            value={this.state.deviceId}
                        />
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.handleClose}>CANCEL</Button>
                    <Button onClick={this.handleSubmit}>UPDATE</Button>
                </DialogActions>
            </Dialog>
        );
    }
}

const mapStateToProps = state =>{
    return {
        deviceId: state.deviceId
    }
}

const mapDispatchToProps = {
    setDeviceId
}

const Settings = connect(mapStateToProps, mapDispatchToProps, null, {forwardRef:true})(SettingsDialog)
export default Settings




