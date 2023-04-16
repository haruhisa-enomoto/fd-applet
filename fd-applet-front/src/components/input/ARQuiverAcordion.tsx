import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Typography,
} from "@mui/material";

import GenericQuiver from "../common/GenericQuiver";

export default function ARQuiverAccordion() {
  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>AR quiver</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <GenericQuiver url="ar-quiver" updateButton />
      </AccordionDetails>
    </Accordion>
  );
}
