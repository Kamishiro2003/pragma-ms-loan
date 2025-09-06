package co.com.loan.model.page;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageCommand {

  private int page;
  private int size;
}
