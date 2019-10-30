import React from 'react';
import {makeStyles} from '@material-ui/core/styles';
import Modal from '@material-ui/core/Modal';
import Backdrop from '@material-ui/core/Backdrop';
import Fade from '@material-ui/core/Fade';
import {hideModal} from "../actions/patientActions";
import {connect} from "react-redux";
import AddNewPatientModalContent from "./AddNewPatientModalContent";

const useStyles = makeStyles(theme => ({
  modal: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  paper: {
    backgroundColor: theme.palette.background,
    // border: '2px solid #000',
    boxShadow: theme.shadows[5],
    padding: theme.spacing(2, 4, 3),
  },
}));

const mapStateToProps = (state) => {
  return {
    patientModal: state.patientReducer.modal,
  }
};

const mapDispatchToProps = dispatch => ({
  hideModal: () => dispatch(hideModal()),
});


export default connect(mapStateToProps, mapDispatchToProps)(function TransisionModal(props) {
    const classes = useStyles();

    const handleClose = () => {
      props.hideModal()
    };

    console.log("render modal");
    return (
      <Modal
        aria-labelledby="transition-modal-title"
        aria-describedby="transition-modal-description"
        className={classes.modal}
        open={props.patientModal.isOpen}
        onClose={handleClose}
        closeAfterTransition
        BackdropComponent={Backdrop}
        BackdropProps={{
          timeout: 500,
        }}
      >
        <Fade in={props.patientModal.isOpen}>
          <div className={classes.paper}>


            <h2 id="transition-modal-title">Transition modal</h2>
            <p id="transition-modal-description">react-transition-group animates me.</p>

            <AddNewPatientModalContent/>

          </div>
        </Fade>
      </Modal>
    );
  }
)
