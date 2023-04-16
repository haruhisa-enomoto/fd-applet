import React from "react";

import FeedbackIcon from "@mui/icons-material/Feedback";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import MenuIcon from "@mui/icons-material/Menu";
import SaveIcon from "@mui/icons-material/Save";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import { Button, Divider, Menu, MenuItem } from "@mui/material";
import ListItemIcon from "@mui/material/ListItemIcon/ListItemIcon";
import ListItemText from "@mui/material/ListItemText/ListItemText";

interface FileMenuProps {
  save: () => void;
  open: () => void;
  handleNakayama: () => void;
  report: () => void;
}

export default function FileMenu(props: FileMenuProps) {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <div>
      <Button
        aria-controls="file-menu"
        aria-haspopup="menu"
        onClick={handleClick}
        startIcon={<MenuIcon />}
      >
        File
      </Button>
      <Menu
        id="file-menu"
        anchorEl={anchorEl}
        keepMounted
        open={Boolean(anchorEl)}
        onClose={handleClose}
      >
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            props.open();
          }}
        >
          <ListItemIcon>
            <UploadFileIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Open</ListItemText>
        </MenuItem>
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            props.save();
          }}
        >
          <ListItemIcon>
            <SaveIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Save</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            props.handleNakayama();
          }}
        >
          <ListItemText>Nakayama algebra from Kupisch series</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            window.open(
              "https://haruhisa-enomoto.github.io/fd-applet/#usage",
              "_blank"
            );
          }}
        >
          <ListItemIcon>
            <HelpOutlineIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Help</ListItemText>
        </MenuItem>
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            window.open(
              "https://haruhisa-enomoto.github.io/fd-applet-ja/#%E4%BD%BF%E7%94%A8%E6%96%B9%E6%B3%95",
              "_blank"
            );
          }}
        >
          <ListItemIcon>
            <HelpOutlineIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Help (Japanese)</ListItemText>
        </MenuItem>
        <Divider />
        <MenuItem
          onClick={() => {
            setAnchorEl(null);
            props.report();
          }}
        >
          <ListItemIcon>
            <FeedbackIcon fontSize="small" />
          </ListItemIcon>
          <ListItemText>Send feedback or report issues</ListItemText>
        </MenuItem>
      </Menu>
    </div>
  );
}
