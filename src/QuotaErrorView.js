import React from 'react'
import {Typography} from "@material-ui/core";

export default class QuotaErrorView extends React.Component{

    render() {
        return (
            <div style={{marginTop:150}}>
                <Typography variant={"h2"} style={{color:'#797979'}}>Daily Quota Exceeded</Typography>
                <Typography variant={"subtitle2"}>Quota will be reset at 12 midnight Pacific Standard Time</Typography>
            </div>
        );
    }

}