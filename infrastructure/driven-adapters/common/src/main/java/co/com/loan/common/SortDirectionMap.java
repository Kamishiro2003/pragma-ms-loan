package co.com.loan.common;

import co.com.loan.model.page.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortDirectionMap {

  public Sort.Direction toSpringDirection(SortDirection direction) {
    return direction == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
  }
}
