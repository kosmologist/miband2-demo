import React, {Fragment} from 'react';
import {initFirebase, listenForHeartBeats} from "./fire";
import Dashboard from "./DashboardView";
import {AppBar, Grid, IconButton, LinearProgress, Toolbar, Typography} from "@material-ui/core";
import Pulse from "./PulseMonitor";
import Presence from "./PresenceMonitor";
import {SettingsOutlined, FitnessCenterOutlined} from "@material-ui/icons";
import Setting from "./SettingsDialog";
import DeviceListView from "./DeviceListView";
import {store} from './store/DataStore'
import QuotaErrorView from "./QuotaErrorView";


export default class App extends React.Component {

    constructor(props) {
        super(props);
        initFirebase();
        this.settings = React.createRef()
        this.state = {isReady: false, quotaExceeded:false}
        store.subscribe(() => {
            const quotaExceeded = store.getState().quotaExceeded
            if (quotaExceeded) {
                this.setState({quotaExceeded:true, isReady:false})
            } else {
                const deviceId = store.getState().deviceId
                if (deviceId) {
                    this.setState({isReady: true})
                    listenForHeartBeats()
                }
            }
        })
    }

    showSettingsModal = () => {
        console.log('Showing settings modal')
        this.settings.current.handleOpen();
    }

    getContentView = () => {
        return (
            <Fragment>
                <Setting ref={this.settings}/>
                <AppBar style={{backgroundColor: '#ffffff'}} position={"static"}>
                    <Toolbar>
                        <IconButton><FitnessCenterOutlined/></IconButton>
                        <Typography variant={"h6"}>Demo</Typography>
                        <Typography variant={"body2"} style={{marginLeft: 10, flexGrow: 1}}>0.5.0</Typography>
                        <IconButton onClick={this.showSettingsModal}><SettingsOutlined/></IconButton>
                    </Toolbar>
                </AppBar>
                <Grid container>
                    <Grid item xs={2}>
                        {/*<Presence/>*/}
                    </Grid>
                    <Grid item xs={8}>
                        <Dashboard/>
                    </Grid>
                    <Grid item xs={2}>
                        <Pulse/>
                    </Grid>
                </Grid>
            </Fragment>

        )
    }

    getDeviceListView = () => {
        return (
            <Grid container>
                <Grid item xs={4}></Grid>
                <Grid item xs={4}>
                    <DeviceListView/>
                </Grid>
                <Grid item xs={4}></Grid>
            </Grid>
        )
    }

    getQuotaExceededView = ()=>{
        return (
            <Grid container>
                <Grid item xs={1}></Grid>
                <Grid item xs={10}>
                    <QuotaErrorView/>
                </Grid>
                <Grid item xs={1}></Grid>
            </Grid>
        )
    }

    render() {
        return this.state.quotaExceeded ?
            this.getQuotaExceededView() : this.state.isReady ? this.getContentView() : this.getDeviceListView()
    }
}

