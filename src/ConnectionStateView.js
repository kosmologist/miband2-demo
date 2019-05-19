import React from "react";
import Typography from "@material-ui/core/Typography";
import {CancelOutlined, CheckCircleOutline} from "@material-ui/icons";
import {store} from './store/DataStore'

export default class ConnectionStateView extends React.Component {

    constructor(props) {
        super(props)
        this.state = {
            status: '',
            since: 0
        }
        store.subscribe(() => {
            const presence = store.getState().status;
            if (presence){
                this.setState({
                    status: presence.status,
                    since: presence.timestamp
                })
            }
        })
    }

    render() {
        const isOnline = this.state.status === 'online'
        const since = this.state.since
        return (
            <div style={{display: 'inline-flex', alignItems: 'center'}}>
                {isOnline ? <CheckCircleOutline style={{color: '#00e676'}}/> : <CancelOutlined style={{color: '#ff5252'}}/>}
                <Typography variant={"subtitle2"} style={{marginLeft: 10}}>{isOnline ? 'Online' : 'Offline'} since {new Date(since).toLocaleString()}</Typography>
            </div>
        );
    }

}