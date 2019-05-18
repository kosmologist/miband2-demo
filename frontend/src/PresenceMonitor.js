import React from "react";
import Typography from "@material-ui/core/Typography";
import {Button, List, ListItem, ListItemText} from "@material-ui/core";
import {deleteAttendanceLogs} from "./fire";
import {connect} from "react-redux";

class PresenceMonitor extends React.Component {

    getAttendance = () => {
        return this.props.attendance.map(presence => {
            return (
                <ListItem style={{padding: 0}} key={presence.timestamp}>
                    <ListItemText
                        primary={
                            <Typography style={{color: '#797979'}}>
                                {presence.status + ' (' + new Date(presence.timestamp).toLocaleString() + ')'}
                            </Typography>
                        }
                    />
                </ListItem>
            )
        })
    }

    onLogsDelete = () => {
        deleteAttendanceLogs()
    }

    render() {
        return (
            <div style={{
                color: '#424242',
                height: 700,
                overflow: "auto",
                marginTop: 20,
                marginLeft: 20,
                textAlign: 'center'
            }}>
                <Typography variant={"h6"} style={{color: '#676767'}}>Presence</Typography>
                <Button variant={"outlined"} style={{marginTop: 10, marginBottom: 10}}
                        onClick={() => this.onLogsDelete()}>Delete Logs</Button>
                <List dense>
                    {this.getAttendance()}
                </List>
            </div>
        );
    }

}

const mapStateToProps = state => {
    return {
        attendance: state.attendance ? state.attendance : []
    }
};

const Presence = connect(mapStateToProps)(PresenceMonitor)
export default Presence