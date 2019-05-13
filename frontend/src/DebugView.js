import React from "react";
import {store} from "./store/DataStore";
import Typography from "@material-ui/core/Typography";
import {List, ListItem, ListItemText} from "@material-ui/core";

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
                <ListItem>
                    <ListItemText
                        primary={beat.beat}
                        secondary={Date(beat.timestamp).toString()}
                    />
                </ListItem>
            )
        })
    }

    render() {
        return (
            <div style={{backgroundColor:"#E7E7E7", color:'#424242', height: 700, overflow:"auto"}}>
                <Typography>Debug View</Typography>
                <List dense>
                    {this.getBeats()}
                </List>
            </div>
        );
    }
}