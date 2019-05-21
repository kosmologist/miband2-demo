import React from "react";
import Typography from "@material-ui/core/Typography";
import {Button, List, ListItem, ListItemText} from "@material-ui/core";
import {deleteHeartBeatLogs} from "./fire";
import {connect} from "react-redux";

class PulseMonitor extends React.Component {

    getBeats = () => {
        return this.props.beats.map(beat => {
            return (
                <ListItem style={{padding: 0}} key={beat.timestamp}>
                    <ListItemText
                        primary={
                            <Typography style={{color: '#797979'}}>
                                {beat.beat + ' (' + new Date(beat.timestamp).toLocaleString() + ')'}
                            </Typography>
                        }
                    />
                </ListItem>
            )
        })
    }

    onLogsDelete = () => {
        console.log('Logs Deleting')
        deleteHeartBeatLogs()
    }

    render() {
        return (
            <div style={{color: '#424242', height: 700, overflow: "auto", marginTop: 20, textAlign: 'center'}}>
                <Typography variant={"h6"} style={{color: '#676767'}}>Pulse</Typography>
                <Typography variant={"body1"}>Note: Stop streaming data before deleting logs to avoid unexpected
                    results</Typography>
                <Button variant={"outlined"} style={{marginTop: 10, marginBottom: 10}}
                        onClick={() => this.onLogsDelete()}>Delete Logs</Button>
                <List dense>
                    {this.getBeats()}
                </List>
            </div>
        );
    }
}

const mapStateToProps = state => {
    return {beats: state.beats ? state.beats : []}
};

const Pulse = connect(mapStateToProps)(PulseMonitor);
export default Pulse