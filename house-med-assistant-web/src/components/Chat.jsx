import React, {Component} from 'react'


const styles = theme => ({
  container: {
    display: 'flex',
    flexWrap: 'wrap',
  },
  textField: {
    marginLeft: theme.spacing.unit,
    marginRight: theme.spacing.unit,
    width: 200,
  },
  button: {
    margin: theme.spacing.unit * 2,
  },
  snackbar: {
    margin: theme.spacing.unit,
  }
})

export default class Chat extends Component {

  // constructor(props) {
  //   super(props)
  //
  //   this.state = {
  //     messages: ["lala", "aa"]
  //   }
  // }
  //   const sock = new SockJS('http://localhost:9000/chat');
  //   // const sock = new SockJS('https://chat-server.azurewebsites.net/chat');
  //
  //   sock.onopen = () => {
  //     console.log("onopen")
  //   }
  //
  //
  //   sock.onmessage = e => {
  //     let data = JSON.parse(e.data).data
  //     this.setState({messages: [data, ...this.state.messages]});
  //     console.log("onmessage", e.data, data)
  //   };
  //
  //   sock.onclose = () => {
  //     console.log("onclose")
  //   }
  //
  //   this.sock = sock;
  //
  //   this.handleFormSubmit = this.handleFormSubmit.bind(this);
  // }
  //
  //
  // handleFormSubmit(e) {
  //   e.preventDefault();
  //   console.log("handleFormSubmit")
  //   // this.sock.send(JSON.stringify({type: "say", data:e.target[0].value}));
  //   this.sock.send(JSON.stringify({type: "jul", data: e.target[0].value}));
  // }

  render() {
    // const { classes, target, messages, handleMessage, access_token} = this.props

    console.log("render Chat");
    return (
      <div>
        <form onSubmit={this.handleFormSubmit}>
          <input type="text" placeholder="Type here to chat..."/>
          <button type="submit">Send</button>
        </form>
        {
          this.state.messages.map((message, index) => {
            return <div key={index}>{message}</div>
          })
        }
      </div>
    )
  }
}
