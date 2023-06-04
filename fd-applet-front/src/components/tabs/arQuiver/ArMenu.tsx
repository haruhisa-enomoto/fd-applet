import * as React from 'react';
import { useState } from 'react';

import FormatColorFillIcon from '@mui/icons-material/FormatColorFill';
import SettingsIcon from '@mui/icons-material/Settings';
import { Box, ListItemIcon, ListItemText, Slider, Stack, Typography } from '@mui/material';
import Button from '@mui/material/Button';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';

import { useSelection } from '../../../contexts/SelectionContext';
import useFetchWithUiFeedback from '../../../hooks/useFetchWithUiFeedback';
import { defaultPhysicsOptions } from '../../common/QuiverOptions';

interface ArMenuProps {
  physics: any;
  setPhysics: React.Dispatch<React.SetStateAction<any>>;
}

export default function ArMenu(
  { physics, setPhysics }: ArMenuProps
) {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [showPhysics, setShowPhysics] = useState(false);

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleColor = async () => {
    setAnchorEl(null);
    const responseProj = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/proj",
      showSuccess: false,
    });
    const responseInj = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/inj",
      showSuccess: false,
    });
    if (responseProj.data === undefined) return;
    if (responseInj.data === undefined) return;
    setSecondarySelected(responseProj.data);
    setSelected(responseInj.data);
    setHighlighted([]);
  };

  const handleChangePhysics = () => {
    setAnchorEl(null);
    setShowPhysics(true);
  };

  return (
    <>
      <Box sx={{
        float: "right",
        textAlign: "right",
      }}>
        <Button onClick={handleClick}>
          AR Quiver Menu
        </Button>
        <Menu
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
        >
          <MenuItem onClick={handleColor}>
            <ListItemIcon>
              <FormatColorFillIcon />
            </ListItemIcon>
            <ListItemText>
              Color projs (red) and injs (blue)
            </ListItemText>
          </MenuItem>
          <MenuItem onClick={handleChangePhysics}>
            <ListItemIcon>
              <SettingsIcon />
            </ListItemIcon>
            <ListItemText>
              Change physics settings
            </ListItemText>
          </MenuItem>
        </Menu>
      </Box>
      {showPhysics &&
        <>
          <Typography>Gravitational constant</Typography>
          <Slider
            value={physics.barnesHut.gravitationalConstant}
            onChange={(event, newValue) => {
              setPhysics({
                ...physics,
                barnesHut: {
                  ...physics.barnesHut,
                  gravitationalConstant: newValue,
                },
              });
            }}
            min={-20000}
            max={-100}
            step={100}
            valueLabelDisplay="auto" />
          <Typography>Spring length</Typography>
          <Slider
            value={physics.barnesHut.springLength}
            onChange={(event, newValue) => {
              setPhysics({
                ...physics,
                barnesHut: {
                  ...physics.barnesHut,
                  springLength: newValue,
                },
              });
            }}
            min={0}
            max={500}
            step={10}
            valueLabelDisplay="auto" />
          <Typography>Spring constant</Typography>
          <Slider
            value={physics.barnesHut.springConstant}
            onChange={(event, newValue) => {
              setPhysics({
                ...physics,
                barnesHut: {
                  ...physics.barnesHut,
                  springConstant: newValue,
                },
              });
            }}
            min={0.01}
            max={1}
            step={0.01}
            valueLabelDisplay="auto" />
          <Stack direction="row" spacing={2} justifyContent="space-around">
            <Button variant="outlined" onClick={() => setShowPhysics(false)}>
              Close
            </Button>
            <Button variant="outlined" onClick={() => setPhysics(defaultPhysicsOptions)}>
              Reset to Default
            </Button>
          </Stack>
        </>
      }
    </>
  );
}

