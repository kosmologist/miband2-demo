import React from "react";
import {store} from "./store/DataStore";
import Typography from "@material-ui/core/Typography";
import {Button, List, ListItem, ListItemText} from "@material-ui/core";

export default class DebugView extends React.Component{

    constructor(props){
        super(props)
        this.state = {beats:[]}
        store.subscribe(()=>{
            this.setState({
                beats: store.getState().beats
            })
        })
    }

    getBeats = ()=>{
        return this.state.beats.map(beat=>{
            return (
                <ListItem style={{padding:0}}>
                    <ListItemText
                        primary={
                            <Typography style={{color:'#797979'}}>
                                {beat.beat + ' (' + new Date(beat.timestamp).toLocaleString() + ')'}
                            </Typography>
                        }
                    />
                </ListItem>
            )
        })
    }

    render() {
        return (
            <div style={{ color:'#424242', height: 700, overflow:"auto"}}>
                <Typography variant={"h6"}>Debug View</Typography>
                <Button>Delete Logs</Button>
                <List dense>
                    {this.getBeats()}
                </List>
            </div>
        );
    }
}